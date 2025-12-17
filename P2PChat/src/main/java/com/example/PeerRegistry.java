package com.example;

import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PeerRegistry {
    private final Set<OutputStream> allConnections;
    private final Map<String, OutputStream> namedPeers;

    public PeerRegistry() {
        this.allConnections = ConcurrentHashMap.newKeySet();
        this.namedPeers = new ConcurrentHashMap<>();
    }

    public void register(OutputStream out) {
        allConnections.add(out);
    }

    public void registerNamed(String name, OutputStream out) {
        namedPeers.put(name, out);
    }

    public void unregister(OutputStream out) {
        allConnections.remove(out);
        namedPeers.values().remove(out);
    }

    public OutputStream getPeer(String name) {
        return namedPeers.get(name);
    }

    public Set<OutputStream> getAll() {
        return allConnections;
    }

    public Set<String> getAllNamedPeers() {
        return this.namedPeers.keySet();
    }

    public void clear() {
        allConnections.clear();
        namedPeers.clear();
    }
}
