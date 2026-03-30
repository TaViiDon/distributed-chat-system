# Lab Report: Distributed Communication and Coordination

## 1. Problem Description and Objectives
*Describe the goal of building a distributed chat system and what you intended to achieve.*

## 2. Design and Architecture
*Include a flowchart or diagram (can be ASCII or a link to an image in this folder).*
- **Communication Model:** Client-Server using TCP Sockets.
- **Message Handling:** Multi-threaded server for concurrent client management.
- **Synchronization:** Thread-safe access to the client writer set.

## 3. Implementation Details
- **Language:** Java 11+
- **Libraries:** java.net, java.io, java.util
- **Setup Steps:** 
  1. Compile ChatServer and ChatClient.
  2. Start ChatServer on a specific port.
  3. Start multiple instances of ChatClient to test broadcasting.

## 4. Output Screenshots or Logs
*Paste logs from the server and multiple clients here.*

## 5. Discussion of Results
- **Latency:** Observations on message delivery speed.
- **Ordering:** How timestamps help maintain chronological order.
- **Coordination Behavior:** How the server manages multiple clients simultaneously.

## 6. Conclusion
*Summary of what was learned about message-passing and distributed systems.*
