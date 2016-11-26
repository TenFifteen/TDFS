package org.tridiots.tdfs;

import org.tridiots.ipc.RPC;
import org.tridiots.ipc.Server;

public class NameNode implements DataNodeProtocol {

    public static final int PORT = 54321;

    private Server server;

    public NameNode() {
        this.server = RPC.getServer(this, PORT);
    }

    public void join() {
        this.server.join();
    }

    public void start() {
        this.server.start();
    }

    public void stop() {
        this.server.stop();
    }

    @Override
    public String sendHeartBeat(String sender) {
        System.out.println("get heartbeat from " + sender);
        return "hello";
    }

    public static void main(String args[]) {
        System.out.println("starting NameNode ...");
        NameNode namenode = new NameNode();
        namenode.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                namenode.stop();
            }
        });

        System.out.println("NameNode started");
        namenode.join();
    }
}
