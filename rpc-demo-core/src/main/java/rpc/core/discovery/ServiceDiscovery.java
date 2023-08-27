package rpc.core.discovery;

import rpc.core.entity.BaseRequest;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress lookup(BaseRequest request) throws Exception;

}
