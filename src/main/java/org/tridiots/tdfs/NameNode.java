package org.tridiots.tdfs;

import org.tridiots.ipc.RPC;
import org.tridiots.ipc.Server;

public class NameNode implements DataNodeProtocol {

    public static final int PORT = 54321;

    private Server server;

    public NameNode() {
        this.server = RPC.getServer(this, PORT);
        if (this.server != null) {
            this.server.start();
        }
    }

    public void join() {
        try {
            this.server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sendHeartBeat(String sender) {
        System.out.println("get heartbeat from " + sender);
        return "hello";
    }

    public static void main(String args[]) {
        System.out.println("starting NameNode ...");
        
        NameNode namenode = new NameNode();
        namenode.join();
    }
}
