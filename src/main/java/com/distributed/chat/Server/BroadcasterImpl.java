/*
    Author: Rackeel Brooks
*/

package com.distributed.chat.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.ArrayList;


public class BroadcasterImpl extends UnicastRemoteObject implements Broadcaster {
    private static ArrayList<Recipient> broadcastList = new ArrayList<>();

    BroadcasterImpl() throws RemoteException { super(); } 

    private Boolean isRecipientAlreadyPresent(Recipient recipient){
        Boolean isPresent = false;

        for (Recipient listedRecipient : broadcastList) {
                isPresent = (listedRecipient.equals(recipient)) ? true : false;
        }
        return isPresent;
    }

    public Integer registerRecipient(Recipient recipient) {
        Boolean isPresent =  isRecipientAlreadyPresent(recipient);
        Integer isRegistrationSuccessful = -1;

        if(isPresent == false){
            
            broadcastList.add(recipient);
            isRegistrationSuccessful = 1;

            System.out.println(recipient.toString());

            // BroadcasterImpl.broadcastList.forEach(registeredRecipient -> {
            //     System.out.println("\nRegistered Recipient: \n" + registeredRecipient.toString());
            // });
        }

        return isRegistrationSuccessful;
    }

    public void sendBroadcastMessage(Recipient sender, String message) throws RemoteException {
        if (broadcastList.isEmpty()) {
            System.out.println("Recipients must first be registered");
            return;
        }

        if (BroadcasterImpl.broadcastList.size() == 1 && BroadcasterImpl.broadcastList.contains(sender)) {
            System.out.println("No other recipients to broadcast to");
            return;
        }

        for(Recipient listedRecipient : broadcastList) {
            if(!sender.equals(listedRecipient)) {
                listedRecipient.recipientReceiveMessage(sender, message);
                System.out.println(sender + " Received message");
            }
        }

        System.out.println("Message broadcasted successfully✅");

    }

    @Override
    public ArrayList<Recipient> getBroadcastList() {
        return broadcastList;
    }

    
}