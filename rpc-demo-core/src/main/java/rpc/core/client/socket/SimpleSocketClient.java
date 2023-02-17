package rpc.core.client.socket;

import rpc.core.client.ClientInterface;
import rpc.core.entity.BaseRequest;
import rpc.core.exception.RpcException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * client base on simple Socket
 */
public class SimpleSocketClient implements ClientInterface {

    private final static int PORT = 9998;

    @Override
    public Object sendRpcRequest(BaseRequest rpcRequest) {
        // TODO using ZooKeeper to discover service ip and port
        InetSocketAddress address = null;
        try {
            address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
        } catch (UnknownHostException e) {
            throw new RpcException("get host address error");
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
