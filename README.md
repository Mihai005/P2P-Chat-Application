# 💬 Java P2P Chat Application

![Java](https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Protobuf](https://img.shields.io/badge/Protobuf-3.x-blue?style=for-the-badge&logo=google&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean-green?style=for-the-badge)

A robust, decentralized Peer-to-Peer (P2P) chat application built with **Java Sockets**, **Protocol Buffers**, and **Virtual Threads**. 

This project demonstrates **Clean Architecture** principles, separating the Console UI from the Networking logic, and implements multiple design patterns to handle concurrency and state management effectively.

## ✨ Key Features

* **Decentralized Architecture:** Every node acts as both a Client and a Server.
* **Structured Protocol:** Uses Google Protocol Buffers (Protobuf 3) for efficient, typed binary message serialization.
* **Virtual Threads:** Utilizes modern Java Virtual Threads (`newVirtualThreadPerTaskExecutor`) for high-throughput, lightweight concurrency.
* **Robust Handshake:** Implements a strict State Machine protocol (`HELLO` -> `ACK` -> `TEXT`) to verify peers before chatting.
* **Interactive CLI:** A shell-like interface supported by the **Strategy Pattern** for extensible commands.
* **Graceful Shutdown:** Broadcasts `BYE` messages to all peers to ensure clean disconnection handling.

## 🏗️ Architecture & Design Patterns

This project moves away from the "God Class" anti-pattern by enforcing a strict separation of concerns:

### 1. Clean Architecture Layers
* **UI Layer (`ConsoleApp`):** Handles user input and command parsing. Completely decoupled from network logic.
* **Logic Layer (`NetworkService`):** Manages connection lifecycle, message routing, and peer coordination.
* **Data Layer (`PeerRegistry`):** A thread-safe component responsible for maintaining the state of active connections (`Map<String, OutputStream>`).
* **Infrastructure (`ChatNode`, `PeerHandler`):** Manages raw TCP sockets and protocol enforcement.

### 2. Design Patterns Used
* **Strategy Pattern:** Used in the CLI to encapsulate commands (`!hello`, `!send`, `!bye`) into separate classes, making the application Open/Closed (OCP) compliant.
* **State Machine:** The `PeerHandler` implements a strict state machine (Handshake Phase vs. Chat Phase) to ensure protocol integrity.
* **Factory Pattern:** A `MessageFactory` isolates the complexity of building Protobuf objects.

## 🚀 Getting Started

### Prerequisites
* **Java 24** (Required SDK)
* **Gradle 8.x** (Configured in wrapper)

### Installation
1.  Clone the repository:
    ```bash
    git clone https://github.com/Mihai005/P2P-Chat-Application.git
    cd P2PChat
    ```
2.  Build the project (this will auto-generate Protobuf Java files):
    ```bash
    ./gradlew installDist
    ```

### Running the App
To simulate a chat, open two terminal windows.

**Terminal 1 (Alice - Port 5000):**
```bash
./build/install/P2PChat/bin/P2PChat.bat 5000
# or for Mac/Linux:
# ./build/install/P2PChat/bin/P2PChat 5000
```

**Terminal 2 (Bob - Port 6000):**
```bash
./build/install/P2PChat/bin/P2PChat.bat 6000
```

## 🎮 Usage Commands

Once the application is running, use the following commands in the shell:

| Command | Usage | Description |
| :--- | :--- | :--- |
| **`!hello`** | `!hello <ip>:<port>` | Initiates a TCP connection and Handshake with a peer. |
| **`!send`** | `!send <name> <msg>` | Sends a private message to a specific peer. |
| **`!bye`** | `!bye <name>` | Disconnects from a specific peer gracefully. |
| **`!byebye`** | `!byebye` | Broadcasts disconnect to ALL peers and shuts down the app. |

### Example Session

**Alice (Port 5000):**
```text
> !hello 127.0.0.1:6000
Connected to 127.0.0.1:6000.
[System]: Student6000 said hello. Sending ACK.
```

**Bob (Port 6000):**
```text
[System]: Connection verified by Student5000.
> !send Student5000 Hey Alice!
```

## 📡 Protocol Specification

The application uses a custom Protobuf schema defined in `chat.proto`:

```protobuf
message ChatMessage {
  enum MessageType {
    HELLO = 0; // Sent immediately on connection
    ACK = 1;   // Response to confirm identity
    TEXT = 2;  // Standard chat message
    BYE = 3;   // Graceful disconnect signal
  }
  MessageType type = 1;
  string senderName = 2;
  string content = 3;
  string recipientName = 4;
}
```

## 🧪 Testing

The project includes unit tests using **JUnit 5** and **Mockito**.

* **Logic Tests:** Verify message creation and parsing.
* **State Tests:** Verify the `PeerRegistry` correctly adds/removes peers.

Run tests via Gradle:
```bash
./gradlew test
```

## 📂 Project Structure

```text
src/main/
├── proto/
│   └── chat.proto               # Protocol Buffer Definition
└── java/com/example/
    ├── Main.java                # Entry Point (Strategy Context)
    ├── ChatNode.java            # Server Container
    ├── NetworkService.java      # Business Logic
    ├── PeerHandler.java         # Connection Thread (State Machine)
    ├── PeerRegistry.java        # Thread-Safe State Management
    ├── MessageFactory.java      # Helper for Protobufs
    └── commands/                # Strategy Implementations
        ├── HelloCommand.java
        ├── SendCommand.java
        └── ...
```

---
