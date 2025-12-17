package example;

import com.example.ChatProtobufs;
import com.example.ChatProtobufs.ChatMessage;
import com.example.MessageFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageFactoryTest {

    @Test
    void createHello_shouldSetCorrectTypeAndSender() {
        ChatProtobufs.ChatMessage msg = MessageFactory.createHello("Student5000");

        assertEquals(ChatMessage.MessageType.HELLO, msg.getType());
        assertEquals("Student5000", msg.getSenderName());
    }

    @Test
    void createText_shouldIncludeRecipientAndContent() {
        ChatProtobufs.ChatMessage msg = MessageFactory.createText("Alice", "Bob", "Hello World");

        assertEquals(ChatProtobufs.ChatMessage.MessageType.TEXT, msg.getType());
        assertEquals("Alice", msg.getSenderName());
        assertEquals("Bob", msg.getRecipientName());
        assertEquals("Hello World", msg.getContent());
    }
}
