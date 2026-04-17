package com.distributed.chat;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * ChatServer - The main entry point for the distributed chat server.
 *
 * This class starts an RMI registry on a given port, creates one
 * BroadcasterImpl object, and binds it under the name "broadcast".
 * Once running, clients can connect and register themselves as Recipients.
 *
 * How to run:
 *   java com.distributed.chat.ChatServer <port>
 *   e.g.  java com.distributed.chat.ChatServer 1099
 */
public class ChatServer {

    public static void main(String[] args) {

        // Default port is 1099 (standard RMI port), but we let the user override it
        int port = 1099;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port argument, using default port 1099.");
            }
        }

        try {
            // Create the broadcaster — this is the object all clients will call remotely
            BroadcasterImpl broadcaster = new BroadcasterImpl();

            // Start the RMI registry on the chosen port
            // Think of this like opening a phone book that clients can look up services in
            Registry registry = LocateRegistry.createRegistry(port);

            // Bind the broadcaster to the name "broadcast" so clients can find it
            registry.bind("broadcast", broadcaster);

            System.out.println("=== Chat Server started on port " + port + " ===");
            System.out.println("Waiting for clients to connect...");
            System.out.println("(Press Ctrl+C to shut down the server)");

        } catch (Exception e) {
            System.out.println("Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
