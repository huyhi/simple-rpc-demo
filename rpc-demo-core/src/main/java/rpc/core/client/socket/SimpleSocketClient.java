package rpc.core.client.socket;

import rpc.core.client.ClientInterface;
import rpc.core.discovery.ServiceDiscovery;
import rpc.core.discovery.ZkServiceDiscovery;
import rpc.core.entity.BaseRequest;
import rpc.core.exception.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * client base on simple Socket
 */
public class SimpleSocketClient implements ClientInterface {

    private ServiceDiscovery serviceDiscovery;

    public SimpleSocketClient() {
        this.serviceDiscovery = new ZkServiceDiscovery();
    }

    @Override
    public Object sendRpcRequest(BaseRequest rpcRequest) {
        InetSocketAddress address;
        try {
            address = serviceDiscovery.lookup(rpcRequest);
        } catch (Exception e) {
            throw new RpcException("discover host address error", e);
        }

        try (Socket socket = new Socket()) {
            socket.connect(address);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // send rpc request to the server
            objectOutputStream.writeObject(rpcRequest);
            // read response from the server
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException(String.format("call remote failed. address: %s, request: %s", address, rpcRequest), e);
        }
    }
}
