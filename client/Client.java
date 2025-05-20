package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER = "localhost";
    private static final int PORT = 6000;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER, PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in);
        ) {
            System.out.println(input.readLine());

            System.out.println("Klienti u mbyll.");
        } catch (IOException e) {
            System.out.println("Nuk mund te lidhet me serverin.");
        }
    }
}
