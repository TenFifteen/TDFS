package org.tridiots.ipc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private InetSocketAddress addr;
    private SocketChannel sendChannel = null;

    public Client(InetSocketAddress addr) {
            this.addr = addr;
    }

    public Object call(String methodName, Class<?>[] paramTypes, Object[] params) {
        Param param = new Param(methodName, paramTypes, params);

        try {
            this.sendChannel = SocketChannel.open(addr);
            //this.sendChannel.configureBlocking(false);
            SocketObjectUtil.sendObject(sendChannel, param);
            this.sendChannel.socket().shutdownOutput();
            Object result = SocketObjectUtil.receiveObject(sendChannel);
            this.sendChannel.close();

            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
