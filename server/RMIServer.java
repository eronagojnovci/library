package server;

import shared.LibraryService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            LibraryService service = new LibraryServiceImpl();
            Registry registry = LocateRegistry.createRegistry(6001);
            registry.rebind("LibraryService", service);
            System.out.println("RMI Serveri eshte gati.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
