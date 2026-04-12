package com.distributed.chat.Server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            BroadcasterImpl broadcasterImpl = new BroadcasterImpl();
            String apiEndpoint = "rmi://localhost:";
            Integer port = 1900;
            LocateRegistry.createRegistry(port);
            apiEndpoint = apiEndpoint + Integer.toString(port) + "/broadcast";
            Naming.rebind(apiEndpoint, broadcasterImpl);
            System.out.println("Server Listening.......");
        }catch(Exception e) {
            System.out.println(e);
        }
    }
    
}
