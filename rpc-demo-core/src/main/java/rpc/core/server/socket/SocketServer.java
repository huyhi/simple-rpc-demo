package rpc.core.server.socket;

import lombok.extern.slf4j.Slf4j;
import rpc.core.server.ServerInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SocketServer implements ServerInterface {

    public static final int PORT = 9998;

    public void start() {

        try (ServerSocket server = new ServerSocket()) {

            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));

            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                // TODO handle response
            }
        } catch (IOException e) {
            log.error("start socket server error. ", e);
        }
    }
}
