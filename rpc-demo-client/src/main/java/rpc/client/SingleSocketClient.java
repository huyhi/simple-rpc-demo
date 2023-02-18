package rpc.client;

import lombok.extern.slf4j.Slf4j;
import rpc.api.UserService;
import rpc.core.client.socket.SimpleSocketClient;
import rpc.core.proxy.ClientProxy;
import rpc.dto.User;

import java.util.Random;

@Slf4j
public class SingleSocketClient {

    public static void main(String[] args) {
        /*
         * how could client call a remote procedure just like call a local simple function
         * use a jdk proxy to achieve that, actually the service variable is a proxy object
         */
        UserService service = new ClientProxy(new SimpleSocketClient()).getProxy(UserService.class);

        for (int i = 0; i < 10; i++) {
            User userData = service.queryById(new Random().nextLong());
            log.info("response: {}", userData);
        }
    }
}
