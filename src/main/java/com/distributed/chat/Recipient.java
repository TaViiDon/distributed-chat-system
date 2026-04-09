package com.distributed.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Recipient interface - to be implemented 

public interface Recipient extends Remote {
    void RecipientReceiveMessage(String message) throws RemoteException;
}
