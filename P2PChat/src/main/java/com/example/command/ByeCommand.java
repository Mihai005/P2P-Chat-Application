package com.example.command;

import com.example.ChatNode;

public class ByeCommand implements ICommand {
    @Override
    public void execute(String command, ChatNode node) {
        String[] parts = command.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("Usage: !bye <username>");
            return;
        }
        String targetName = parts[1];
        node.getNetwork().disconnectPeer(targetName);
    }

    @Override
    public String toString() {
        return "  !bye <username>            -> Disconnect from peer";
    }
}
