package rpc.core.discovery;

import rpc.core.entity.BaseRequest;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress lookup(String serviceName) throws Exception;

    InetSocketAddress lookup(BaseRequest request) throws Exception;

}
