package com.example.command;

import com.example.ChatNode;

public class HelloCommand implements ICommand {
    @Override
    public void execute(String command, ChatNode node) {
        try {
            String[] parts = command.split(" ");
            String[] address = parts[1].split(":");
            String ip = address[0];
            int targetPort = Integer.parseInt(address[1]);

            node.getNetwork().connectToPeer(ip, targetPort);
        } catch (Exception e) {
            System.out.println("Invalid format. Use: !hello <ip>:<port>");
        }
    }

    @Override
    public String toString() {
        return "  !hello <ip>:<port>         -> Connect to peer";
    }
}
