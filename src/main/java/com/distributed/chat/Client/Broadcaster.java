package com.distributed.chat.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

// Broadcaster interface - to be implemented
public interface Broadcaster extends Remote {
    List<Recipient> getBroadcastList() throws RemoteException;
    Integer registerRecipient(Recipient recipient) throws RemoteException;
    void sendBroadcastMessage(Recipient sender, String message) throws RemoteException;
}
