package com.distributed.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Recipient - Remote interface for a chat client.
 *
 * Any class that implements this interface can receive messages from the server.
 * The server holds a stub (remote reference) to each connected client and calls
 * RecipientReceiveMessage() on them whenever a broadcast comes in.
 *
 * This is the "callback" pattern in RMI — normally the client calls the server,
 * but here the server also calls back to the client to deliver incoming messages.
 */
public interface Recipient extends Remote {

    /**
     * RecipientReceiveMessage - Called by the server to deliver a message to this client.
     *
     * The message will already have a timestamp prepended by the server,
     * e.g. "[2025-04-16 14:32:01] Hello everyone!"
     *
     * @param message  the timestamped message string to display
     * @throws RemoteException if the network call fails (e.g. client went offline)
     */
    void RecipientReceiveMessage(String message) throws RemoteException;
}
