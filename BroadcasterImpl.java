package com.distributed.chat;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class BroadcasterImpl extends UnicastRemoteObject implements Broadcaster {

    private ArrayList<Recipient> recipients;

    protected BroadcasterImpl() throws RemoteException {
        recipients = new ArrayList<>();
    }

    @Override
    public int registerRecipient(Recipient recipient) throws RemoteException {
        recipients.add(recipient);
        System.out.println("Recipient registered.");
        return recipients.size();
    }

    @Override
    public void sendBroadcastMessage(Recipient sender, String message) throws RemoteException {
        System.out.println("Broadcasting message: " + message);

        for (Recipient r : recipients) {
            if (r != sender) { // optional: don’t send back to sender
                r.RecipientReceiveMessage(message);
            }
        }
    }
}
