package rpc.core.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    void registerService(String serviceName, InetSocketAddress address) throws Exception;

}
