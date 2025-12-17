package com.example.command;

import com.example.ChatNode;

public interface ICommand {
    void execute(String command, ChatNode node);
}
