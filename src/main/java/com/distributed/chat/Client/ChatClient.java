package com.distributed.chat.Client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.distributed.chat.Server.Broadcaster;
import com.distributed.chat.Server.Recipient;

//ChatClient - Client-side component of the distributed RMI chat system.

// Implements the Recipient interface so it can receive remote callbacks from the Broadcaster.
public class ChatClient extends UnicastRemoteObject implements Recipient {

    // Local storage for received messages, each entry formatted as "[timestamp] raw_message"
    private List<String> receivedMessages;

    // Constructor
    /**
     * Creates the ChatClient and exports it as a remote object.
     * Calling super() on UnicastRemoteObject registers this instance with the
     * RMI runtime so it can receive network callbacks from the Broadcaster.
     *
     * @throws RemoteException if the RMI export fails (e.g. port already in use)
     */
    public ChatClient() throws RemoteException {
        super();
        this.receivedMessages = new ArrayList<>();
    }

    // Recipient interface implementation  (called REMOTELY by the Broadcaster)
    @Override
    public void recipientReceiveMessage(Recipient sender , String message) throws RemoteException {
        // Capture the moment this client received the message for ordering and review purposes
        String timestamp    = LocalDateTime.now()
                                           .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String formattedEntry = "[" + timestamp + "] " + message;
        
        // Persist the message locally so the user can review it via option 2
        receivedMessages.add(formattedEntry);

        // Notify the user mid-session that a new message has arrived
        System.out.println("\n[Incoming] " + formattedEntry);
        System.out.print("SELECT 1 TO BROADCAST OR 2 TO VIEW BROADCAST: ");
    }

    // Client helper methods
    public void checkBroadcast() {
        if (receivedMessages.isEmpty()) {
            System.out.println("No broadcast messages received yet.");
            return;
        }

        System.out.println("\n=== Received Broadcasts (" + receivedMessages.size() + ") ===");
        for (int i = 0; i < receivedMessages.size(); i++) {
            // 1-indexed numbering for readability
            System.out.println((i + 1) + ". " + receivedMessages.get(i));
        }
        System.out.println("========================================");
    }
    
    private static int getPortNumber(Scanner scanner) {
        System.out.print("Enter server port number: ");
        return Integer.parseInt(scanner.nextLine().trim());
    }

    // Helper method to connect to the Broadcaster via RMI using the provided endpoint string
    private static Broadcaster accessApiEndpoint(String apiEndpoint) throws Exception {
        // Parse the port from "path:<port>/broadcast"
        int portStart   = apiEndpoint.indexOf(':') + 1;
        int portEnd     = apiEndpoint.indexOf('/');
        int port        = Integer.parseInt(apiEndpoint.substring(portStart, portEnd));

        // Parse the service name ("broadcast") that the server bound in the registry
        String serviceName = apiEndpoint.substring(portEnd + 1);

        // Locate the RMI registry running on the server at the extracted port
        Registry registry = LocateRegistry.getRegistry("localhost", port);

        // Look up and return the Broadcaster stub registered under serviceName
        return (Broadcaster) registry.lookup(serviceName);
    }

    // Main entry point
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        // Scanner for user input (port number, menu options, broadcast messages)
        try {
            // Variable declarations (matching pseudocode names and types)
            Broadcaster  broadcasterConnection;   // handle to the remote Broadcaster
            ChatClient   client;                  // this node, acting as a Recipient
            String       apiEndpoint;             // RMI endpoint string
            int          port;                    // server registry port
            String       message;                 // message text to broadcast
            int          option;                  // user's menu selection

            // Build the endpoint and connect to the Broadcaster
            // SET port TO get_port_number
            port = getPortNumber(scanner);

            // SET api_endpoint TO "path:" + port + "/broadcast"
            apiEndpoint = "path:" + port + "/broadcast";

            // SET broadcaster_connection TO access_api_endpoint(api_endpoint)
            broadcasterConnection = accessApiEndpoint(apiEndpoint);

            // Create this client as a Recipient and register with the Broadcaster

            // SET client TO Recipient()
            client = new ChatClient();

            // broadcaster_connection.registerRecipient(client)
            // Check return value — negative means registration was rejected
            if (broadcasterConnection.registerRecipient(client) < 0) {
                System.out.println("FAILED TO REGISTER TO BROADCASTER, TRY AGAIN");
            }


            // Display the list of registered recipients

            // DO-WHILE menu loop  (exit condition: option == 99)
            do {
                System.out.println("SELECT 1 TO BROADCAST , 2 TO VIEW BROADCAST ");
                option = Integer.parseInt(scanner.nextLine().trim());

                // IF option == 1 THEN — send a broadcast message
                if (option == 1) {
                    System.out.println("ENTER BROADCAST MESSAGE: ");
                    message = scanner.nextLine().trim();
                    // Pass this client as sender so the Broadcaster can exclude it
                    broadcasterConnection.sendBroadcastMessage(client, message);
                }

                // IF option == 2 THEN — view all received broadcasts
                if (option == 2) {
                    client.checkBroadcast();
                }

                // IF option != 1 AND option != 2 THEN — invalid selection
                if (option != 1 && option != 2) {
                    System.out.println("INVALID INPUT\n");
                    System.out.println(option + "\n");
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
