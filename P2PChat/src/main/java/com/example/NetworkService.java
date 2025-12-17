package com.example;

import com.example.ChatProtobufs.ChatMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class NetworkService {
    private final String myName;
    private final PeerRegistry registry;
    private final ExecutorService pool;
    private final ChatNode node;

    public NetworkService(String myName, PeerRegistry registry, ExecutorService pool, ChatNode node) {
        this.myName = myName;
        this.registry = registry;
        this.pool = pool;
        this.node = node;
    }

    public void connectToPeer(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            OutputStream output = socket.getOutputStream();

            MessageFactory.createHello(myName).writeDelimitedTo(output);

            registry.register(output);

            System.out.println("Connected to " + ip + ":" + port + ". Waiting for handshake...");

            pool.execute(new PeerHandler(socket, node, myName));

        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public void sendPrivateMessage(String targetName, String content) {
        OutputStream out = registry.getPeer(targetName);

        if (out == null) {
            System.out.println("[Error]: Unknown peer '" + targetName + "'. Connected peers: " + registry.getAllNamedPeers());
            return;
        }

        try {
            MessageFactory.createText(myName, targetName, content).writeDelimitedTo(out);

        } catch (IOException e) {
            System.out.println("Failed to send to " + targetName);
            registry.unregister(out);
        }
    }

    public void disconnectPeer(String targetName) {
        OutputStream out = registry.getPeer(targetName);
        if (out == null) {
            System.out.println("[Error]: You are not connected to '" + targetName + "'.");
            return;
        }

        try {
            MessageFactory.createBye(myName, targetName).writeDelimitedTo(out);

            out.close();
            registry.unregister(out);

            System.out.println("[System]: Disconnected from " + targetName);
        } catch (IOException e) {
            System.out.println("[Error]: Failed to disconnect cleanly: " + e.getMessage());
            registry.unregister(out);
        }
    }

    public void broadcastShutdown() {
        ChatMessage bye = MessageFactory.createBye(myName, "All");

        for(OutputStream out : registry.getAll()) {
            try {
                bye.writeDelimitedTo(out);
                out.close();
            } catch (IOException e) {}
        }
        registry.clear();
    }
}
