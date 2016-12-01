package org.tridiots.ipc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int THREAD_NUMBER = 4;
    private ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUMBER);
    private Object instance;
    private volatile boolean running = true;

    private InetSocketAddress address = null;
    private ServerSocketChannel accpetChannel = null;
    private Selector selector = null;

    public Server(Object instance, int port) throws IOException {
        this.instance = instance;
        this.address = new InetSocketAddress("localhost", port);

        this.accpetChannel = ServerSocketChannel.open();
        this.accpetChannel.configureBlocking(false);
        this.accpetChannel.socket().bind(address);

        this.selector = Selector.open();
        this.accpetChannel.register(selector, SelectionKey.OP_ACCEPT);

        logger.info("Server start listen to port:" + address.getHostName() + ":" + address.getPort());
    }

    private void doProcess(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel channel = server.accept();

        Param param = (Param)SocketObjectUtil.receiveObject(channel);
        Object result = call(param);
        SocketObjectUtil.sendObject(channel, result);
        channel.shutdownOutput(); // otherwise the client will block on channel.read
    }
    @Override
    public void run() {
        while (running) {
            try {
                if (selector.select() == 0) continue;
                Iterator<SelectionKey> iter = this.selector.selectedKeys().iterator();

                while (iter.hasNext()) {
                    SelectionKey key  = iter.next();
                    iter.remove();

                    pool.submit(() -> {
                        try {
                            doProcess(key);
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    });
                }

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
