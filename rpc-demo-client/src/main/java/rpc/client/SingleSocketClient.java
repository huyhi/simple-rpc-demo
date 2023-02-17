package rpc.client;

import lombok.extern.slf4j.Slf4j;
import rpc.api.HelloService;
import rpc.core.client.ClientInterface;
import rpc.core.client.socket.SimpleSocketClient;
import rpc.core.entity.BaseRequest;

@Slf4j
public class SingleSocketClient {

    public static void main(String[] args) {

        ClientInterface client = new SimpleSocketClient();

        // TODO use JDK dynamic proxy to enhance api call
        BaseRequest request = BaseRequest.builder()
                .interfaceName(HelloService.class.getName())
                .methodName("sayHello")
                .parameters(new Object[]{"hello world"})
                .parameterTypes(new Class<?>[]{String.class})
                .build();

        Object resp = client.sendRpcRequest(request);
        log.info("response: {}", resp);
    }
}
