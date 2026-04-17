package com.distributed.chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BroadcasterImpl - The server-side implementation of the Broadcaster interface.
 *
 * This class keeps a list of all connected clients (Recipients) and handles
 * two things:
 *   1. Registering new clients when they join the chat.
 *   2. Broadcasting messages from one client to all other clients.
 *
 * We use CopyOnWriteArrayList instead of a regular ArrayList because multiple
 * clients can connect at the same time (concurrency), and we need to make sure
 * the list doesn't get corrupted when two threads modify it simultaneously.
 */
public class BroadcasterImpl extends UnicastRemoteObject implements Broadcaster {

    // Thread-safe list of every client currently registered with this server
    private final CopyOnWriteArrayList<Recipient> recipients;

    // Formatter used to stamp each broadcast with the time it was sent
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor — just initialises the recipients list.
     * The throws clause is required by UnicastRemoteObject.
     */
    protected BroadcasterImpl() throws RemoteException {
        recipients = new CopyOnWriteArrayList<>();
    }

    /**
     * registerRecipient - Called by a client when it first connects.
     *
     * The client passes a reference to itself (a Recipient stub) so the server
     * can call back on it later when a broadcast arrives.
     *
     * @param recipient  the remote stub for the connecting client
     * @return the total number of clients now registered, or -1 on error
     */
    @Override
    public int registerRecipient(Recipient recipient) throws RemoteException {
        recipients.add(recipient);
        int count = recipients.size();
        System.out.println("[Server] New client joined. Total connected: " + count);
        return count;
    }

    /**
     * sendBroadcastMessage - Called by a client when it wants to send a message.
     *
     * The server prepends a timestamp to the message so every client sees
     * the same ordered timestamp, then forwards it to every OTHER client.
     *
     * We skip the sender so they don't receive their own message back.
     * Note: we use .equals() here, NOT != , because in RMI the sender arrives
     * as a stub object — reference equality (!=) would fail across the network.
     *
     * @param sender   the client that sent the message (excluded from delivery)
     * @param message  the raw text the sender typed
     */
    @Override
    public void sendBroadcastMessage(Recipient sender, String message) throws RemoteException {
        // Build a timestamped version of the message so all clients see message ordering
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String broadcastMessage = "[" + timestamp + "] " + message;

        System.out.println("[Server] Broadcasting: " + broadcastMessage);

        // Loop through every registered client and deliver the message
        for (Recipient r : recipients) {
            // Skip the sender — they already know what they typed
            if (!r.equals(sender)) {
                try {
                    r.RecipientReceiveMessage(broadcastMessage);
                } catch (RemoteException e) {
                    // Client probably disconnected — remove them from the list
                    System.out.println("[Server] A client disconnected, removing from list.");
                    recipients.remove(r);
                }
            }
        }
    }
}
