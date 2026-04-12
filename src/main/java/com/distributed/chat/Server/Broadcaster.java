package com.distributed.chat.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Broadcaster interface - to be implemented
public interface Broadcaster extends Remote {
    Integer registerRecipient(Recipient recipient) throws RemoteException;
    void sendBroadcastMessage(Recipient sender, String message) throws RemoteException;
}
