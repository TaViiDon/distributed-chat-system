# Distributed Chat System (Java RMI)

## Overview
A client-server chat application built with **Java RMI (Remote Method Invocation)**.  
Multiple clients can connect to one server and broadcast messages to each other in real time.  
Every message is automatically timestamped by the server so message ordering is clear.

---

## Architecture

```
Client A  ──┐
            ├──► BroadcasterImpl (Server) ──► delivers to all other clients
Client B  ──┘
```

| Class | Role |
|---|---|
| `ChatServer` | Starts the RMI registry and binds the Broadcaster |
| `BroadcasterImpl` | Holds the client list; handles register + broadcast |
| `ChatClient` | Connects to server, sends messages, receives broadcasts |
| `Broadcaster` | Remote interface the client calls on the server |
| `Recipient` | Remote interface the server calls back on each client |

---

## Prerequisites
- **JDK 11 or higher**

---

## Compiling

From the project root, compile all source files at once:

```bash
javac -d out src/main/java/com/distributed/chat/*.java
```

This places all `.class` files into an `out/` folder.

---

## Running

### 1. Start the server (Terminal 1)
```bash
java -cp out com.distributed.chat.ChatServer 1099
```
> `1099` is the port number. You can use any free port.

### 2. Start Client 1 (Terminal 2)
```bash
java -cp out com.distributed.chat.ChatClient
```
When prompted, enter the **same port** the server is running on (e.g. `1099`).

### 3. Start Client 2 (Terminal 3)
```bash
java -cp out com.distributed.chat.ChatClient
```
Same steps — connect to port `1099`.

---

## Usage (Client Menu)
```
SELECT 1 TO BROADCAST OR 2 TO VIEW BROADCAST, OR 99 TO EXIT
1   → type a message and press Enter to send it to all other clients
2   → view all messages you have received so far
99  → disconnect and exit
```

---

## Message Format
All messages delivered to clients look like:
```
[Incoming] [2025-04-16 14:32:01] Hello everyone!
```
The timestamp is added by the server when it broadcasts, so every client sees the same time.
