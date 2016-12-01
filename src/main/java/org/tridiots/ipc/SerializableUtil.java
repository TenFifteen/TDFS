package org.tridiots.ipc;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableUtil {
    private static final Logger logger = LoggerFactory.getLogger(SerializableUtil.class);
    public static byte[] toBytes(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
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
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return object;
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