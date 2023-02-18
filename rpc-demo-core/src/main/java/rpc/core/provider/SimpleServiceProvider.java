package rpc.core.provider;

import lombok.extern.slf4j.Slf4j;
import rpc.core.entity.RpcServiceConfig;
import rpc.core.enums.RpcErrMsgEnum;
import rpc.core.exception.RpcException;
import rpc.core.registry.ServiceRegistry;
import rpc.core.registry.ZKServiceRegistry;
import rpc.core.server.socket.SimpleSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SimpleServiceProvider implements ServiceProvider {

    private ServiceRegistry serviceRegistry;
    /**
     * key: service name
     * value: service object
     */
    private final Map<String, Object> serviceMap;

    public SimpleServiceProvider() {
        this.serviceMap = new ConcurrentHashMap<>();
        this.serviceRegistry = new ZKServiceRegistry();
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcErrMsgEnum.SERVICE_NOT_FOUNT, serviceName);
        }
        return service;
    }

    @Override
    public void addService(RpcServiceConfig serviceConfig) {
        String serviceName = serviceConfig.getServiceName();
        if (serviceMap.containsKey(serviceName)) {
            log.info("service: {} already exist.", serviceName);
            return;
        }
        serviceMap.put(serviceName, serviceConfig.getService());
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        // set service to zk
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            serviceRegistry.registerService(rpcServiceConfig.getServiceName(), new InetSocketAddress(host, SimpleSocketServer.PORT));
        } catch (Exception e) {
            throw new RpcException("publish service error.", e);
        }
        addService(rpcServiceConfig);
    }
}
