package org.tridiots.tdfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tridiots.ipc.RPC;
import org.tridiots.ipc.Server;

public class NameNode implements DataNodeProtocol {
    private static final Logger logger = LoggerFactory.getLogger(NameNode.class);
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
        logger.info("get heartbeat from " + sender);
        return "hello";
    }

    public static void main(String args[]) {
        logger.info("starting NameNode ...");
        NameNode namenode = new NameNode();
        namenode.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                namenode.stop();
            }
        });

        logger.info("NameNode started");
        namenode.join();
    }
}
