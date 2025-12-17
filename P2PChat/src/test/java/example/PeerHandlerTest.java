package example;

import com.example.ChatNode;
import com.example.PeerHandler;
import com.example.PeerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class PeerHandlerTest {

    @Mock
    Socket mockSocket;
    @Mock
    ChatNode mockNode;
    @Mock
    PeerRegistry mockRegistry;

    ByteArrayOutputStream outputBuffer;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mockNode.getRegistry()).thenReturn(mockRegistry);

        InetAddress mockAddr = mock(InetAddress.class);
        when(mockSocket.getInetAddress()).thenReturn(mockAddr);
        when(mockAddr.getHostAddress()).thenReturn("127.0.0.1");

        outputBuffer = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputBuffer);
    }

    @Test
    void run_shouldRegisterPeer_whenHelloReceived() throws IOException {
        ByteArrayInputStream inputData = createInputMessage(
                ChatProtobufs.ChatMessage.MessageType.HELLO, "ClientBob", null
        );
        when(mockSocket.getInputStream()).thenReturn(inputData);

        PeerHandler handler = new PeerHandler(mockSocket, mockNode, "Server");
        handler.run();

        verify(mockRegistry).registerNamed(eq("ClientBob"), any());

        ChatProtobufs.ChatMessage response = ChatProtobufs.ChatMessage.parseDelimitedFrom(
                new ByteArrayInputStream(outputBuffer.toByteArray())
        );
        assertSame(ChatProtobufs.ChatMessage.MessageType.ACK, response.getType());
    }

    @Test
    void run_shouldCloseSocket_whenProtocolViolated() throws IOException {
        ByteArrayInputStream inputData = createInputMessage(
                ChatProtobufs.ChatMessage.MessageType.TEXT, "ClientBob", "Bad Message"
        );
        when(mockSocket.getInputStream()).thenReturn(inputData);

        PeerHandler handler = new PeerHandler(mockSocket, mockNode, "Server");
        handler.run();

        verify(mockSocket, atLeastOnce()).close();
        verify(mockRegistry, never()).registerNamed(anyString(), any());
    }

    private ByteArrayInputStream createInputMessage(ChatProtobufs.ChatMessage.MessageType type, String sender, String content) throws IOException {
        ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
        ChatProtobufs.ChatMessage.Builder builder = ChatProtobufs.ChatMessage.newBuilder()
                .setType(type)
                .setSenderName(sender);

        if (content != null) builder.setContent(content);

        builder.build().writeDelimitedTo(tempOut);

        return new ByteArrayInputStream(tempOut.toByteArray());
    }
}