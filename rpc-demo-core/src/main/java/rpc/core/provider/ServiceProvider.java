package rpc.core.provider;

import rpc.core.entity.RpcServiceConfig;

public interface ServiceProvider {

    Object getService(String serviceName);

    void addService(RpcServiceConfig serviceConfig);

    void publishService(RpcServiceConfig rpcServiceConfig);

}
