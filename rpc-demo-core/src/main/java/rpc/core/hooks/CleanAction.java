package rpc.core.hooks;

import lombok.extern.slf4j.Slf4j;
import rpc.core.server.socket.SimpleSocketServer;
import rpc.core.utils.CuratorUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Slf4j
public class CleanAction implements Runnable {

    @Override
    public void run() {
        log.info("shutdown hook has run.");
        // clean registered zk info
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), SimpleSocketServer.PORT);
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
        } catch (Exception e) {
            log.error("shutdown hook runner error. clean zk info failed. ", e);
        }
    }
}
