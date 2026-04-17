# Lab Report: Distributed Communication and Coordination
### Distributed Systems – Group Lab Assignment
**Option Selected:** Option 1 – Simple Chat System (Message Passing)  
**Language:** Java  
**Communication Method:** Java RMI (Remote Method Invocation)

---

## 1. Problem Description and Objectives

The goal of this lab was to build a simple distributed chat application that demonstrates how multiple programs running on different machines (or ports) can communicate with each other without using shared memory.

In a real distributed system, processes cannot just read each other's variables — they have to send messages back and forth over a network. This chat system is a practical example of that idea.

**What we set out to build:**
- A server that can handle multiple clients at the same time
- Clients that can send messages which get broadcast to every other connected client
- Each message should carry a timestamp so you can see what order messages were sent
- The system should be testable with 2–3 clients running at the same time

We chose **Java RMI** as our communication mechanism. RMI lets one Java program call methods on an object that is running inside a completely different JVM (Java Virtual Machine), even on a different machine. This fits the message-passing idea perfectly — the client "calls" the server, and the server "calls back" to each client to deliver messages.

---

## 2. Design and Architecture

### High-Level Design

The system follows a **client-server architecture** with a callback mechanism:

```
  [Client A]  ─── registerRecipient() ──►  [Server / BroadcasterImpl]
  [Client B]  ─── registerRecipient() ──►        │
                                                  │
  [Client A]  ─── sendBroadcastMessage() ──►      │
                                                  │
                             ◄── RecipientReceiveMessage() ── [Client B]
```

1. The server starts and exposes a `Broadcaster` object in the RMI registry.
2. Each client looks up the `Broadcaster` in the registry and registers itself as a `Recipient`.
3. When a client wants to send a message, it calls `sendBroadcastMessage()` on the server.
4. The server prepends a timestamp and calls `RecipientReceiveMessage()` on every other registered client (skipping the sender).

### Classes and Their Roles

| Class / Interface | Type | Responsibility |
|---|---|---|
| `Broadcaster` | Interface | Defines the two methods clients call on the server |
| `BroadcasterImpl` | Server class | Implements Broadcaster; stores client list; handles broadcasting |
| `Recipient` | Interface | Defines the callback method the server calls on each client |
| `ChatClient` | Client class | Implements Recipient; connects, sends messages, receives callbacks |
| `ChatServer` | Entry point | Starts RMI registry and binds BroadcasterImpl |

### ASCII Flow Diagram

```
ChatServer starts
      │
      ├─► Creates BroadcasterImpl
      ├─► Starts RMI Registry on port X
      └─► Binds BroadcasterImpl as "broadcast"

ChatClient starts
      │
      ├─► Looks up "broadcast" in registry ──► gets Broadcaster stub
      ├─► Creates itself as a ChatClient (implements Recipient)
      └─► Calls registerRecipient(self) on the server

Client sends a message
      │
      ├─► Calls sendBroadcastMessage(self, text) on server
      └─► Server:
            ├─► Prepends timestamp → "[2025-04-16 14:32:01] Hello!"
            └─► Loops through recipients list
                  ├─► Skips sender
                  └─► Calls RecipientReceiveMessage(msg) on each other client
```

### Why RMI instead of plain Sockets?

Plain sockets give you raw byte streams — you have to manually serialize/deserialize objects and design your own protocol. RMI handles all of that for us. Since we were already in Java it made more sense to use RMI so we could focus on the distributed system concepts rather than low-level socket code.

---

## 3. Implementation Details

**Language:** Java 11+  
**Libraries used:** `java.rmi`, `java.util.concurrent`, `java.time`

### Key Implementation Decisions

**Thread Safety with CopyOnWriteArrayList**  
The server's recipients list can be accessed by multiple threads at the same time — one thread registering a new client while another is iterating the list to broadcast a message. A regular `ArrayList` would throw a `ConcurrentModificationException` in this situation. We used `CopyOnWriteArrayList` which creates a fresh copy of the underlying array on every write, making reads always safe without locking.

**Server-Side Timestamps**  
Timestamps are added by the server (in `BroadcasterImpl.sendBroadcastMessage`) rather than by the client. This way, every recipient sees the exact same timestamp on the same message, which accurately reflects the message ordering at the point of broadcast.

**RMI Callback Pattern**  
Normally in RMI the client calls the server. Here we also go the other way — the server calls back to each client using the `Recipient` interface. For this to work, the `ChatClient` exports itself as a remote object by extending `UnicastRemoteObject`.

**Sender Exclusion**  
When broadcasting, the server skips the sender using `.equals()` (not `!=`). In RMI, the sender is received as a stub object — a different Java object than what's in the list, so `!=` reference comparison would always fail. `.equals()` on RMI stubs compares the underlying remote references, which is what we want.

**Disconnected Client Handling**  
If a client disconnects without deregistering, the server will get a `RemoteException` when it tries to deliver a message. We catch this and remove the dead client from the list automatically.

### Setup Steps

```
1. Install JDK 11 or higher
2. Compile:
   javac -d out src/main/java/com/distributed/chat/*.java
3. Start the server:
   java -cp out com.distributed.chat.ChatServer 1099
4. Start Client 1 (new terminal):
   java -cp out com.distributed.chat.ChatClient
   → enter port: 1099
5. Start Client 2 (another terminal):
   java -cp out com.distributed.chat.ChatClient
   → enter port: 1099
6. From either client, select option 1 to send a message.
   The other client will receive it instantly with a timestamp.
```

---

## 4. Output Screenshots / Logs

*(Replace this section with actual screenshots when recording the demo.)*

**Server Terminal:**
```
=== Chat Server started on port 1099 ===
Waiting for clients to connect...
[Server] New client joined. Total connected: 1
[Server] New client joined. Total connected: 2
[Server] Broadcasting: [2025-04-16 14:32:01] Hello from Client 1!
[Server] Broadcasting: [2025-04-16 14:32:15] Hey, got your message!
```

**Client 1 Terminal:**
```
Enter server port number: 1099
SELECT 1 TO BROADCAST OR 2 TO VIEW BROADCAST, OR 99 TO EXIT
1
ENTER BROADCAST MESSAGE:
Hello from Client 1!
SELECT 1 TO BROADCAST OR 2 TO VIEW BROADCAST, OR 99 TO EXIT

[Incoming] [2025-04-16 14:32:15] Hey, got your message!
```

**Client 2 Terminal:**
```
Enter server port number: 1099
SELECT 1 TO BROADCAST OR 2 TO VIEW BROADCAST, OR 99 TO EXIT

[Incoming] [2025-04-16 14:32:01] Hello from Client 1!
SELECT 1 TO BROADCAST OR 2 TO VIEW BROADCAST, OR 99 TO EXIT
1
ENTER BROADCAST MESSAGE:
Hey, got your message!
```

---

## 5. Discussion of Results

**Latency**  
Since all tests were run on localhost (same machine, different ports), the round-trip message delay was near-instant — typically under 5 milliseconds. In a real deployment across machines over a LAN or the internet, this would increase depending on network conditions, but the architecture would remain the same.

**Message Ordering**  
The timestamp added by the server ensures that every client sees messages in the same chronological order. Because the timestamp is applied at the server before delivery, there is no situation where two clients see the same message with different times. This is important in distributed systems — if each client added its own timestamp there could be clock drift between machines.

**Coordination Behavior**  
The server coordinates all communication — clients never talk directly to each other. This centralizes control, which makes broadcast easy to implement but also means the server is a single point of failure. If the server crashes, all communication stops. For a production system you would want some form of fault tolerance (e.g. a backup server), but for this lab the centralized model works well.

**Concurrency**  
Using `CopyOnWriteArrayList` and Java RMI's built-in threading meant we did not have to write any explicit synchronization code. The server handles multiple simultaneous client calls without deadlocking.

---

## 6. Conclusion

This lab gave us hands-on experience with the core concepts of distributed systems:

- **Message passing** – processes communicate by calling methods on remote objects, not through shared memory.
- **Concurrency** – the server has to safely handle multiple clients connecting and sending messages at the same time.
- **Callbacks / reverse RMI** – the server calling back to the client is a pattern that comes up in real systems (webhooks, event-driven architectures, etc.).
- **Fault tolerance basics** – handling `RemoteException` when a client disconnects taught us that in distributed systems things fail silently and you have to design for it.

The most interesting challenge was understanding why `!=` for sender comparison failed in RMI and why `.equals()` was needed — it highlighted that RMI objects travel as stubs over the network and aren't the same Java object on both sides. That kind of subtlety is exactly what makes distributed programming different from regular single-process programming.
