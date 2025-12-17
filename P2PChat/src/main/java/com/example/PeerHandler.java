package com.example;

import com.example.ChatProtobufs.ChatMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PeerHandler implements Runnable {
    private final Socket socket;
    private final ChatNode node;
    private final String myName;
    private String peerName;

    public PeerHandler(Socket socket, ChatNode node, String myName) {
        this.socket = socket;
        this.node = node;
        this.myName = myName;
        this.peerName = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        OutputStream output = null;
        try {
            InputStream input = socket.getInputStream();
            output = socket.getOutputStream();

            node.getRegistry().register(output);

            boolean isHandshakeComplete = false;

            while (true) {
                ChatMessage msg = ChatMessage.parseDelimitedFrom(input);
                if (msg == null) break;

                if (!isHandshakeComplete) {
                    if (!processHandshake(msg, output)) {
                        socket.close();
                        return;
                    }
                    isHandshakeComplete = true;
                } else {
                    if (!processChatMessage(msg)) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(peerName + " disconnected: " + e.getMessage());
        } finally {
            cleanup(output);
        }
    }

    private boolean processHandshake(ChatMessage msg, OutputStream output) throws IOException {
        return switch (msg.getType()) {
            case HELLO -> {
                this.peerName = msg.getSenderName();
                System.out.println("[System]: " + peerName + " said hello. Sending ACK.");

                node.getRegistry().registerNamed(peerName, output);
                sendAck(output);
                yield true;
            }

            case ACK -> {
                this.peerName = msg.getSenderName();
                System.out.println("[System]: Connection verified by " + peerName);

                node.getRegistry().registerNamed(peerName, output);
                yield true;
            }

            default -> {
                System.out.println("[Error]: Peer did not follow protocol (Expected HELLO/ACK). Closing.");
                yield false;
            }
        };
    }

    private boolean processChatMessage(ChatMessage msg) {
        return switch (msg.getType()) {
            case TEXT -> {
                System.out.println("\n[" + msg.getSenderName() + "]: " + msg.getContent());
                System.out.print("> ");
                yield true;
            }

            case BYE -> {
                System.out.println("[System]: " + msg.getSenderName() + " disconnected.");
                yield false;
            }

            default -> true;
        };
    }

    private void sendAck(OutputStream output) throws IOException {
        MessageFactory.createAck(myName).writeDelimitedTo(output);
    }

    private void cleanup(OutputStream output) {
        if (output != null) {
            node.getRegistry().unregister(output);
        }
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
