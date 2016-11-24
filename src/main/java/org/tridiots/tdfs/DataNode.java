package org.tridiots.tdfs;

import java.net.InetSocketAddress;
import org.tridiots.ipc.RPC;

public class DataNode extends Thread {

    private DataNodeProtocol namenode;
    private volatile boolean running = true;

    public DataNode(String host, int port) {
        this.namenode = (DataNodeProtocol) RPC.getProxy(DataNodeProtocol.class, new InetSocketAddress(host, port));
    }

    @Override
    public void run() {
        while (running) {
            String result = namenode.sendHeartBeat("I am datanode");
            System.out.println("getting message from namenode: " + result);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {
        System.out.println("starting DataNode ...");

        DataNode datanode = new DataNode("localhost", NameNode.PORT);
        datanode.start();
        datanode.join();
    }
}
