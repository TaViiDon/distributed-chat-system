package com.distributed.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Broadcaster - Remote interface for the chat server.
 *
 * Any class that implements this interface can act as a server.
 * Clients call these methods over the network using Java RMI (Remote Method Invocation),
 * which means the calls look like normal Java method calls but actually travel
 * across the network to the server process.
 *
 * Both methods throw RemoteException because network calls can fail at any time
 * (server crash, network timeout, etc.) — Java RMI requires us to declare this.
 */
public interface Broadcaster extends Remote {

    /**
     * registerRecipient - Lets a client sign up to receive broadcast messages.
     *
     * The client passes a reference to itself so the server can call it back
     * when any other client sends a message.
     *
     * @param recipient  the client registering (passed as a remote stub)
     * @return the number of clients now connected, or -1 if registration failed
     * @throws RemoteException if the network call fails
     */
    int registerRecipient(Recipient recipient) throws RemoteException;

    /**
     * sendBroadcastMessage - Sends a message from one client to all others.
     *
     * The server uses the sender reference to skip delivering the message
     * back to the person who sent it.
     *
     * @param sender   the client sending the message
     * @param message  the text content of the message
     * @throws RemoteException if the network call fails
     */
    void sendBroadcastMessage(Recipient sender, String message) throws RemoteException;
}
