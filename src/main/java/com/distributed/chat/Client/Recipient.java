package com.distributed.chat.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Recipient interface - to be implemented 

public interface Recipient extends Remote {
    void recipientReceiveMessage(Recipient recipient , String message) throws RemoteException;
}
