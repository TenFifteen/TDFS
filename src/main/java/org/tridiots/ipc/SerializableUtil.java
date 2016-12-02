package org.tridiots.ipc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializableUtil {
    private static final Logger logger = LoggerFactory.getLogger(SerializableUtil.class);

    public static byte[] toBytes(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static Object toObject(byte[] bytes) {
        if (bytes == null) return null;

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        } catch (ClassNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
