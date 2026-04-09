# Distributed Chat System (Message Passing)

## Objective
Implement a simple client-server chat system using Java sockets. This demonstrates basic distributed communication and synchronization mechanisms where multiple processes (on different machines or ports) can communicate without shared memory.

## Features
- **Server:** Handles multiple clients simultaneously using multi-threading.
- **Client:** Connects to the server, sends messages, and receives broadcasts from other clients.
- **Message Ordering:** Each message includes a timestamp for tracking order.
- **Broadcast:** Messages from any client are broadcast to all other active clients.

## Architecture
- **ChatServer:** Listens for incoming socket connections and spawns a `ClientHandler` for each client.
- **ClientHandler:** Manages the communication loop for a specific client and communicates with the server's broadcast mechanism.
- **ChatClient:** Provides a CLI for the user to send messages and a separate thread to listen for incoming messages from the server.
- **Message:** A data structure (or serialized string) containing the sender, content, and timestamp.

## Getting Started
### Prerequisites
- JDK 11 or higher.

### Running the Server
```bash
java src/main/java/com/distributed/chat/ChatServer.java
java com.distributed.chat.ChatServer
```

### Running the Client
```bash
javac src/main/java/com/distributed/chat/ChatClient.java
java com.distributed.chat.ChatClient
```

