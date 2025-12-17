package com.example.command;

import com.example.ChatNode;

public class ShutdownCommand implements ICommand {
    @Override
    public void execute(String command, ChatNode node) {
        node.shutdown();
    }

    @Override
    public String toString() {
        return "  !byebye                    -> Quit app";
    }
}
