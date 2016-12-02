package org.tridiots.tdfs;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tridiots.ipc.RPC;

public class DataNode implements Runnable {
    private static  final Logger logger = LoggerFactory.getLogger(DataNode.class);
    private DataNodeProtocol namenode;
    private volatile boolean running = true;

    public DataNode(String host, int port) {
        this.namenode = (DataNodeProtocol) RPC.getProxy(DataNodeProtocol.class, 
                new InetSocketAddress(host, port));
    }

    @Override
    public void run() {
        while (running) {
            String result = namenode.sendHeartBeat("I am datanode");
            logger.info("getting message from namenode: {}.", result);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public synchronized void stop() {
        this.running = false;
        notifyAll();
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

    public static void main(String args[]) throws InterruptedException {
        logger.info("starting DataNode ...");
        DataNode datanode = new DataNode("localhost", NameNode.PORT);
        datanode.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                datanode.stop();
            }
        });

        logger.info("DataNode started.");
        datanode.join();
    }
}
