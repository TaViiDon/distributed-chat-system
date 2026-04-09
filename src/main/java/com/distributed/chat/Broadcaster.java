package com.distributed.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Broadcaster interface - to be implemented
public interface Broadcaster extends Remote {
    int registerRecipient(Recipient recipient) throws RemoteException;
    void sendBroadcastMessage(Recipient sender, String message) throws RemoteException;
}
