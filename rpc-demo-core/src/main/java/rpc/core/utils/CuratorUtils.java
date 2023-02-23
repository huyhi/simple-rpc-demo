package rpc.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import rpc.core.enums.CfgNameEnum;
import rpc.core.enums.RpcErrMsgEnum;
import rpc.core.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtils {

    private static final Set<String> registeredPathSet = ConcurrentHashMap.newKeySet();
    private static final Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();
    // single zk client everywhere
    private static CuratorFramework zkClient;

    private static final String ZK_ADDRESS;
    public static final String ZK_REGISTER_ROOT_PATH;

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    static {
        ZK_ADDRESS = CfgUtils.getCfgAsStr(CfgNameEnum.ZK_ADDRESS);
        ZK_REGISTER_ROOT_PATH = CfgUtils.getCfgAsStr(CfgNameEnum.ZK_ROOT_PATH);
    }

    /*
     * connect to zk server, return the client
     */
    public static CuratorFramework getZkClient() {
        // check zk client already start
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        zkClient =  CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString(ZK_ADDRESS)
                .retryPolicy(new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES))
                .build();
        zkClient.start();

        try {
            // wait 5s until connect to the zookeeper
            if (!zkClient.blockUntilConnected(5, TimeUnit.SECONDS)) {
                throw new RpcException("connect zk timeout");
            }
        } catch (InterruptedException e) {
            throw new RpcException("check zk connection failed. ", e);
        }
        return zkClient;
    }

    public static void createPersistentNode(CuratorFramework zkClient, String path) throws Exception {
        // check local cache first to avoid communicate to zk
        if (registeredPathSet.contains(path)) {
            log.info("path: {} has already registered in zk", path);
            return;
        }

        if (zkClient.checkExists().forPath(path) != null) {
            log.info("path: {} has already registered in zk", path);
        } else {
            // zk path be like: /rpc/rpc.api.UserService/127.0.0.1:9998
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            log.info("zk persistent node was created successfully. node path: {}", path);
        }
        // add to local set cache to enhance performance
        registeredPathSet.add(path);
    }

    public static List<String> getChildrenNode(CuratorFramework zkClient, String serviceName) throws Exception {
        // same, check local cache first
        List<String> serviceList = serviceAddressMap.get(serviceName);
        if (serviceList != null) {
            return serviceList;
        }
        // cache not found, look up in zookeeper
        String prefix = String.format("%s/%s", ZK_REGISTER_ROOT_PATH, serviceName);
        serviceList = zkClient.getChildren().forPath(prefix);
        if (CollectionUtils.isEmpty(serviceList)) {
            throw new RpcException(RpcErrMsgEnum.EMPTY_SERVICE_FROM_ZK, serviceName);
        }
        serviceAddressMap.put(serviceName, serviceList);
        watchService(zkClient, serviceName);
        return serviceList;
    }

    private static void watchService(CuratorFramework zkClient, String serviceName) throws Exception {
        String servicePath = String.format("%s/%s", ZK_REGISTER_ROOT_PATH, serviceName);

        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        pathChildrenCache.getListenable().addListener((curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            serviceAddressMap.put(serviceName, serviceAddresses);
        });
        pathChildrenCache.start();
    }

    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress address) {
        // only clean zk, which cleaned node end with ip
        // parallel method with create streams that execute in multiple threads, make use of multiple processor cores
        registeredPathSet.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(address.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        /*
         * because we have already set a zk path watch on the serviceName
         * do not need to care about clean up serviceAddressMap
         */
    }
}
