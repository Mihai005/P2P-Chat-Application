package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatNode {
    private final int port;
    private final ExecutorService pool;
    private final PeerRegistry registry;
    private final NetworkService networkService;
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;

    public ChatNode(int port) {
        this.port = port;
        this.pool = Executors.newVirtualThreadPerTaskExecutor();
        this.registry = new PeerRegistry();
        this.networkService = new NetworkService("Student" + port, registry, pool, this);
    }

    public void startServer() {
        pool.execute(this::runServerLoop);
    }

    private void runServerLoop() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);
            while (isRunning) {
                try {
                    final Socket clientSocket = serverSocket.accept();
                    pool.execute(new PeerHandler(clientSocket, this, "Student" + port));
                } catch (IOException ex) {
                    if (isRunning) {
                        System.err.println("Failed to accept connection: " + ex.getMessage());
                    }
                }
            }
        } catch (IOException ex) {
            if (isRunning) {
                System.err.println("Could not listen on port " + port);
            }
        }
    }

    public void shutdown() {
        System.out.println("[System]: Shutting down...");
        isRunning = false;

        networkService.broadcastShutdown();
        pool.shutdownNow();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
        }

        System.exit(0);
    }

    public NetworkService getNetwork() {
        return this.networkService;
    }

    public PeerRegistry getRegistry() {
        return this.registry;
    }
}
