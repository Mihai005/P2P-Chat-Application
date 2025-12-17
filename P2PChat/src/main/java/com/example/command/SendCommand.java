package com.example.command;

import com.example.ChatNode;

public class SendCommand implements ICommand {
    @Override
    public void execute(String command, ChatNode node) {
        String[] parts = command.split(" ", 3);
        if (parts.length < 3) {
            System.out.println("Usage: !send <username> <message>");
            return;
        }
        String targetName = parts[1];
        String content = parts[2];

        node.getNetwork().sendPrivateMessage(targetName, content);
    }

    @Override
    public String toString() {
        return "  !send <name> <message...>  -> Send private message";
    }
}
