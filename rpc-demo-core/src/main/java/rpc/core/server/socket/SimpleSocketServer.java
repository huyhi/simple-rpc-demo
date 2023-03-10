package rpc.core.server.socket;

import lombok.extern.slf4j.Slf4j;
import rpc.core.entity.BaseRequest;
import rpc.core.entity.BaseResponse;
import rpc.core.entity.RpcServiceConfig;
import rpc.core.enums.CfgNameEnum;
import rpc.core.exception.RpcException;
import rpc.core.handler.RpcRequestHandler;
import rpc.core.hooks.CleanShutdownHook;
import rpc.core.provider.ServiceProvider;
import rpc.core.provider.SimpleServiceProvider;
import rpc.core.server.ServerInterface;
import rpc.core.utils.CfgUtils;
import rpc.core.utils.SingletonFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SimpleSocketServer implements ServerInterface {

    private final RpcRequestHandler rpcRequestHandler;
    private final ServiceProvider serviceProvider;

    public SimpleSocketServer() {
        this.rpcRequestHandler = new RpcRequestHandler();
        serviceProvider = SingletonFactory.getInstance(SimpleServiceProvider.class);

        // do something for preparation before start
        beforeStart();
    }

    @Override
    public void registerService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }


    private void beforeStart() {
        // load all config properties first
        CfgUtils.loadConfigProperties();
        // register shutdown hook
        CleanShutdownHook.getCleanShutdownHook().clearAll();
    }


    @Override
    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            int port = CfgUtils.getCfgAsInt(CfgNameEnum.SERVICE_PORT);

            server.bind(new InetSocketAddress(host, port));
            log.info("socket server start as {}:{}", host, port);

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
        } catch (Exception e) {
            log.error("handle socket resp meet unexpected exception. ", e);
        }
    }
}
