package rpc.core.server;

import rpc.core.entity.RpcServiceConfig;

public interface ServerInterface {

    void start();

    void registerService(RpcServiceConfig config);

}
