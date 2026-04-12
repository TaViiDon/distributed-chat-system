package com.distributed.chat.Server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class RecipientImpl extends UnicastRemoteObject implements Recipient {

    private ArrayList<String> broadcastMessages;

    protected RecipientImpl() throws RemoteException {
        broadcastMessages = new ArrayList<>();
    }

    @Override
    public void recipientReceiveMessage(Recipient recipient , String message) throws RemoteException {
        broadcastMessages.add(message);
        System.out.println("Received: " + message);
    }
}
