package rpc.core.client;

import rpc.core.entity.BaseRequest;

public interface ClientInterface {

    Object sendRpcRequest(BaseRequest rpcRequest);

}
