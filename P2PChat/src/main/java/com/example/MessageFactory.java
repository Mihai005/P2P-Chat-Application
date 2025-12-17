package com.example;

import com.example.ChatProtobufs.ChatMessage;

public class MessageFactory {
    public static ChatMessage createHello(String senderName) {
        return ChatMessage.newBuilder()
                .setType(ChatMessage.MessageType.HELLO)
                .setSenderName(senderName)
                .build();
    }

    public static ChatMessage createAck(String senderName) {
        return ChatMessage.newBuilder()
                .setType(ChatMessage.MessageType.ACK)
                .setSenderName(senderName)
                .build();
    }

    public static ChatMessage createText(String senderName, String recipient, String content) {
        return ChatMessage.newBuilder()
                .setType(ChatMessage.MessageType.TEXT)
                .setSenderName(senderName)
                .setRecipientName(recipient)
                .setContent(content)
                .build();
    }

    public static ChatMessage createBye(String senderName, String recipient) {
        ChatMessage.Builder builder = ChatMessage.newBuilder()
                .setType(ChatMessage.MessageType.BYE)
                .setSenderName(senderName);

        if (recipient != null) {
            builder.setRecipientName(recipient);
        }

        return builder.build();
    }
}
