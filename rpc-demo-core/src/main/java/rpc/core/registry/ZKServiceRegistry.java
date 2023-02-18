package rpc.core.registry;

import rpc.core.utils.CuratorUtils;

import java.net.InetSocketAddress;

public class ZKServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, InetSocketAddress address) throws Exception {
        String path = String.format(
                "%s/%s%s", CuratorUtils.ZK_REGISTER_ROOT_PATH, serviceName, address.toString()
        );
        CuratorUtils.createPersistentNode(CuratorUtils.getZkClient(), path);
    }
}
