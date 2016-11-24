package org.tridiots.ipc;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private static final int THREAD_NUMBER = 4;

    private ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUMBER);
    private Object instance;
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public Server(Object instance, int port) throws IOException {
        this.instance = instance;
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (running) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("get connection from " + socket.getInetAddress().getHostName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                pool.submit(() -> {
                    while (running) {
                        try {
                            Param param = (Param)ois.readObject();
                            Object result = call(param);
                            oos.writeObject(result);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (EOFException e) {
                            break; // Client closed.
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void join() {
        try {
            while (running) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public synchronized void stop() {
        this.running = false;    
        notifyAll();
    }

    private Object call(Param param) {
        try {
            Method method = this.instance.getClass()
                    .getMethod(param.getMethodName(), param.getParamTypes());
            return method.invoke(instance, param.getParams());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
