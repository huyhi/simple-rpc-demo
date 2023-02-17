package rpc.core.server.socket;

import lombok.extern.slf4j.Slf4j;
import rpc.core.entity.BaseRequest;
import rpc.core.entity.BaseResponse;
import rpc.core.handler.RpcRequestHandler;
import rpc.core.server.ServerInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SocketServer implements ServerInterface {

    public static final int PORT = 9998;

    private final RpcRequestHandler rpcRequestHandler;

    public SocketServer() {
        this.rpcRequestHandler = new RpcRequestHandler();
    }

    public void start() {

        try (ServerSocket server = new ServerSocket()) {

            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));

            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                handleSocket(socket);
            }
        } catch (IOException e) {
            log.error("start socket server error. ", e);
        }
    }

    private void handleSocket(Socket socket) {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            BaseRequest rpcRequest = (BaseRequest) objectInputStream.readObject();
            Object result = rpcRequestHandler.handle(rpcRequest);

            objectOutputStream.writeObject(BaseResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
    }
}
