package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 6000;
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
