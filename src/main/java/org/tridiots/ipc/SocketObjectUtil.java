package org.tridiots.ipc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketObjectUtil {
    private static final Logger logger = LoggerFactory.getLogger(SocketObjectUtil.class);
    public static final int DATA_LENGTH = 1024;

    public static Object receiveObject(SocketChannel channel) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(DATA_LENGTH);

        byte[] bytes = null;
        int size = 0;
        try {
            while ((size = channel.read(buffer)) > 0) {
                bytes = new byte[size];
                buffer.flip();
                buffer.get(bytes);
                baos.write(bytes);
                buffer.clear();
            }

            // important!! read nothing and we should close the channel
            // otherwise we will trap into endless loop
            if (size == -1 && baos.size() == 0) return null;
            bytes = baos.toByteArray();
            Object obj = SerializableUtil.toObject(bytes);

            return obj;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static boolean sendObject(SocketChannel channel, Object obj) {
        byte[] bytes = SerializableUtil.toBytes(obj);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        try {
            channel.write(buffer);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }
}
