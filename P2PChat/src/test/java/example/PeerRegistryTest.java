package example;

import com.example.PeerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PeerRegistryTest {
    private PeerRegistry registry;
    private OutputStream mockStream;

    @BeforeEach
    void setUp() {
        registry = new PeerRegistry();
        mockStream = new ByteArrayOutputStream();
    }

    @Test
    void registerNamed_shouldStoreAndRetrieve() {
        registry.registerNamed("Bob", mockStream);

        OutputStream retrieved = registry.getPeer("Bob");
        assertNotNull(retrieved);
        assertSame(mockStream, retrieved);
    }

    @Test
    void unregister_shouldRemoveFromBothMaps() {
        registry.register(mockStream);
        registry.registerNamed("Alice", mockStream);

        registry.unregister(mockStream);

        assertNull(registry.getPeer("Alice"));
        assertFalse(registry.getAll().contains(mockStream));
    }
}