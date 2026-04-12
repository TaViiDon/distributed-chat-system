/*
    Author: Rackeel Brooks
*/

package com.distributed.chat.Client;

import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.ArrayList;


public class BroadcasterImpl extends UnicastRemoteObject implements Broadcaster {
    private ArrayList<Recipient> broadcastList = new ArrayList<>();

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
        }

        return isRegistrationSuccessful;
    }

    public void sendBroadcastMessage(Recipient sender, String message) throws RemoteException {
        if (broadcastList.isEmpty()) {
            System.out.println("Recipients must first be registered");
            return;
        }

        for(Recipient listedRecipient : broadcastList) {
            if(sender != listedRecipient) {
                RecipientImpl recipientImpl = new RecipientImpl();
                recipientImpl.recipientReceiveMessage(sender , message);
            }
        }

        System.out.println("Message broadcasted successfully✅");

    }


    
}
