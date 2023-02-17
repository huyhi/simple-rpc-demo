package rpc.core.handler;

import lombok.extern.slf4j.Slf4j;
import rpc.core.entity.BaseRequest;
import rpc.core.exception.RpcException;
import rpc.core.provider.ServiceProvider;
import rpc.core.provider.SimpleServiceProvider;
import rpc.core.utils.SingletonFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * how server side handle the request from client
 */
@Slf4j
public class RpcRequestHandler {

    private ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(SimpleServiceProvider.class);
    }

    /*
     * process rpc request, get service -> get method -> invoke method
     */
    public Object handle(BaseRequest request) {
        Object service = serviceProvider.getService(request.getInterfaceName());
        return invokeTargetMethod(request, service);
    }

    private Object invokeTargetMethod(BaseRequest request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            result = method.invoke(service, request.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", request.getInterfaceName(), request.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }

        return result;
    }
}
