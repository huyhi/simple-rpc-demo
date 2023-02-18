package rpc.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import rpc.core.exception.RpcException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtils {

    private static final Set<String> registeredPathSet = ConcurrentHashMap.newKeySet();
    // single zk client everywhere
    private static CuratorFramework zkClient;
    // TODO all config properties should be Configurable
    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/rpc";

    /*
     * connect to zk server, return the client
     */
    public static CuratorFramework getZkClient() {
        // check zk client allready start
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
}
