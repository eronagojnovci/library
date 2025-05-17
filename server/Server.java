package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 12346;
    private static LibraryManager libraryManager = new LibraryManager();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Serveri eshte online ne portin " + PORT);

        ExecutorService pool = Executors.newFixedThreadPool(10);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Klienti lidhur: " + clientSocket.getInetAddress());
            pool.execute(new ClientHandler(clientSocket, libraryManager));
        }
    }
}

