package rpc.server;

import rpc.core.entity.RpcServiceConfig;
import rpc.core.server.ServerInterface;
import rpc.core.server.socket.SimpleSocketServer;
import rpc.server.services.UserServiceImpl;

public class SingleSocketServer {

    public static void main(String[] args) {

        // TODO auto register services
        ServerInterface server = new SimpleSocketServer();
        server.registerService(RpcServiceConfig.builder()
                .service(new UserServiceImpl())
                .build());

        server.start();
    }
}
