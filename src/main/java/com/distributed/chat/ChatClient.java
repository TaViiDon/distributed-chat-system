 package com.distributed.chat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//ChatClient - Client-side component of the distributed RMI chat system.

// Implements the Recipient interface so it can receive remote callbacks from the Broadcaster.
public class ChatClient extends UnicastRemoteObject implements Recipient {

    // Local storage for received messages (already timestamped by the server)
    private List<String> receivedMessages;

    // The display name this client shows to other users
    private String username;

    // Constructor
    /**
     * Creates the ChatClient and exports it as a remote object.
     * Calling super() on UnicastRemoteObject registers this instance with the
     * RMI runtime so it can receive network callbacks from the Broadcaster.
     *
     * @throws RemoteException if the RMI export fails (e.g. port already in use)
     */
    public ChatClient(String username) throws RemoteException {
        super();
        this.username = username;
        this.receivedMessages = new ArrayList<>();
    }

    // Recipient interface implementation  (called REMOTELY by the Broadcaster)
    @Override
    public void RecipientReceiveMessage(String message) throws RemoteException {
        // The server already added a timestamp — just store and display it as-is
        receivedMessages.add(message);

        // Notify the user mid-session that a new message has arrived
        System.out.println("\n[Incoming] " + message);
        System.out.print("SELECT 1 TO BROADCAST, 2 TO VIEW, OR 99 TO EXIT: ");
    }

    // Client helper methods
    public void checkBroadcast() {
        if (receivedMessages.isEmpty()) {
            System.out.println("No broadcast messages received yet.");
            return;
        }

        System.out.println("\n=== Received Broadcasts ===");
        for (int i = 0; i < receivedMessages.size(); i++) {
            // 1-indexed numbering for readability
            System.out.println((i + 1) + ". " + receivedMessages.get(i));
        }
        System.out.println("========================================");
    }
    private static String getServerIP(Scanner scanner) {
        System.out.print("Enter server IP address (or press Enter for localhost): ");
        String input = scanner.nextLine().trim();
        // If they just hit Enter, default to localhost (useful for single-machine testing)
        return input.isEmpty() ? "localhost" : input;
    }

    private static int getPortNumber(Scanner scanner) {
        System.out.print("Enter server port number: ");
        return Integer.parseInt(scanner.nextLine().trim());
    }

    // Helper method to connect to the Broadcaster via RMI using the provided endpoint string
    private static Broadcaster accessApiEndpoint(String apiEndpoint) throws Exception {
        // Parse the host from "host:<port>/broadcast"
        int hostEnd     = apiEndpoint.indexOf(':');
        int portEnd     = apiEndpoint.indexOf('/');
        String host     = apiEndpoint.substring(0, hostEnd);
        int port        = Integer.parseInt(apiEndpoint.substring(hostEnd + 1, portEnd));

        // Parse the service name ("broadcast") that the server bound in the registry
        String serviceName = apiEndpoint.substring(portEnd + 1);

        // Locate the RMI registry running on the server at the given host and port
        Registry registry = LocateRegistry.getRegistry(host, port);

        // Look up and return the Broadcaster stub registered under serviceName
        return (Broadcaster) registry.lookup(serviceName);
    }

    // Main entry point
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        // Scanner for user input (port number, menu options, broadcast messages)
        try {
            // Variable declarations
            Broadcaster  broadcasterConnection;   // handle to the remote Broadcaster
            ChatClient   client;                  // this node, acting as a Recipient
            String       apiEndpoint;             // RMI endpoint string
            String       serverIP;                // IP address of the machine running the server
            int          port;                    // server registry port
            String       username;                // display name shown to other clients
            String       message;                 // message text to broadcast
            int          option;                  // user's menu selection

            // Ask for a username so other clients know who is talking
            System.out.print("Enter your username: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) username = "Anonymous";

            // Build the endpoint and connect to the Broadcaster
            serverIP    = getServerIP(scanner);
            port        = getPortNumber(scanner);

            // Format: "host:port/serviceName"
            apiEndpoint = serverIP + ":" + port + "/broadcast";

            // Look up the Broadcaster on the server
            broadcasterConnection = accessApiEndpoint(apiEndpoint);

            // Create this client as a Recipient and register with the Broadcaster
            client = new ChatClient(username);

            // Check return value — negative means registration was rejected
            if (broadcasterConnection.registerRecipient(client) < 0) {
                System.out.println("FAILED TO REGISTER TO BROADCASTER, TRY AGAIN");
            }

            System.out.println("Connected as \"" + username + "\". Happy chatting!");

            // DO-WHILE menu loop  (exit condition: option == 99)
            do {
                System.out.println("SELECT 1 TO BROADCAST OR 2 TO VIEW BROADCAST, OR 99 TO EXIT");
                option = Integer.parseInt(scanner.nextLine().trim());

                // IF option == 1 THEN — send a broadcast message
                if (option == 1) {
                    System.out.println("ENTER BROADCAST MESSAGE: ");
                    message = scanner.nextLine().trim();
                    // Prepend the username so receivers know who sent it
                    broadcasterConnection.sendBroadcastMessage(client, username + ": " + message);
                }

                // IF option == 2 THEN — view all received broadcasts
                if (option == 2) {
                    client.checkBroadcast();
                }

                // If the user didn't pick 1, 2, or 99 then it's not a valid option
                if (option != 1 && option != 2 && option != 99) {
                    System.out.println("INVALID INPUT - please enter 1, 2, or 99\n");
                }

            } while (option != 99);  // 99 is the exit value to exit the loop

        } catch (Exception e) {
            // Catch-all that surfaces any connection, registration, or I/O error
            System.out.println(e);
        }

        scanner.close();

        // Force JVM exit because the RMI daemon thread keeps the process alive
        System.exit(0);
    }
}
