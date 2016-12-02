package org.tridiots.ipc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private InetSocketAddress addr;
    private SocketChannel clientChannel;

    public Client(InetSocketAddress addr) {
        this.addr = addr;
    }

    public Object call(String methodName, Class<?>[] paramTypes, Object[] params) {
        Param param = new Param(methodName, paramTypes, params);

        try {
            this.clientChannel = SocketChannel.open(addr);

            SocketObjectUtil.sendObject(clientChannel, param);
            clientChannel.socket().shutdownOutput();
            Object result = SocketObjectUtil.receiveObject(clientChannel);
            clientChannel.close();

            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
