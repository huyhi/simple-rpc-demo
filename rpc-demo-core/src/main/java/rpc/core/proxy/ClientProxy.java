package rpc.core.proxy;

import rpc.core.client.ClientInterface;
import rpc.core.entity.BaseRequest;
import rpc.core.entity.BaseResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy implements InvocationHandler {

    private final ClientInterface client;

    public ClientProxy(ClientInterface client) {
        this.client = client;
    }

    /*
     * when a proxy object call a method, actually call below function
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BaseRequest request = BaseRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .build();
        BaseResponse<?> response = (BaseResponse<?>) client.sendRpcRequest(request);
        return response.getData();
    }

    /*
     * use this function to get proxy object
     */
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
}
