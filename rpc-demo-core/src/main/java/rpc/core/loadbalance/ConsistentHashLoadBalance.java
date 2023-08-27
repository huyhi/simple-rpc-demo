package rpc.core.loadbalance;

import rpc.core.entity.BaseRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * reference1: https://www.candicexiao.com/consistenthashing#0340b4f689c145ad8a6d685b3b8fadb8
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected String doSelect(List<String> serviceUrlList, BaseRequest rpcRequest) {
        int identityHashCode = System.identityHashCode(serviceUrlList);

        String rpcServerName = rpcRequest.getInterfaceName();
        ConsistentHashSelector selector = selectors.get(rpcServerName);

        // regenerate selector
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServerName, new ConsistentHashSelector(serviceUrlList, 160, identityHashCode));
            selector = selectors.get(rpcServerName);
        }

        return selector.select(rpcServerName + Arrays.stream(rpcRequest.getParameters()));
    }


    static class ConsistentHashSelector {

        // hash val -> server node
        // use TreeMap to simulate the hash ring
        private final TreeMap<Long, String> virtualInvokers;

        // indicate if the invoker list was changed
        private final int identityHashCode;

        // replicaNumber: virtual node num, 160 as default
        // 使用虚拟节点使得各个节点在环上的分布均匀，降低各个实际节点间的负载差异
        public ConsistentHashSelector(List<String> involkers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (String invoker : involkers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // ketama 算法
                    // 每个节点会生成 40 个字符串，每个字符串再算 16位的 md5 hash 值
                    // 每个 hash 值生成 4 个 4字节的 hash 值，一共 40 * 4 = 160 个 hash 值对应 160 个虚拟节点
                    byte[] digest = md5(String.format("%s", invoker + i));
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        // 从哈希环里面取对应的 Server
        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }
}
