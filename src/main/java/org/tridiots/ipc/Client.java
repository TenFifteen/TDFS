package org.tridiots.ipc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public Client(InetSocketAddress addr) {
        try {
            this.socket = new Socket(addr.getAddress(), addr.getPort());
            // caution: we must create outputsream before inputstream.
            // see: https://stackoverflow.com/questions/5658089/java-creating-a-new-objectinputstream-blocks
            this.oos = new ObjectOutputStream(this.socket.getOutputStream());
            this.ois = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.out.println("connection failed");
            e.printStackTrace();
        }
    }

    public Object call(String methodName, Class<?>[] paramTypes, Object[] params) {
        Param param = new Param(methodName, paramTypes, params);
        try {
            oos.writeObject(param);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
