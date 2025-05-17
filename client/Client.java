package client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER = "localhost";
    private static final int PORT = 12346;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER, PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
        ) {
            // Lexo mesazhin e mirëseardhjes
            System.out.println(input.readLine());

            String command;
            do {
                System.out.println("\nZgjidh veprimin:");
                System.out.println("1. Kerkim libri (KERKO;keyword)");
                System.out.println("2. Huazo libër (HUAZO;bookId)");
                System.out.println("3. Kthe libër (KTHE;bookId)");
                System.out.println("4. Listo librat (LISTO)");
                System.out.println("5. Dil (EXIT)");
                System.out.print("Komanda: ");
                command = scanner.nextLine();

                if (!command.equalsIgnoreCase("EXIT")) {
                    output.println(command);
                    String response = input.readLine();
                    System.out.println("Serveri: " + response);
                }

            } while (!command.equalsIgnoreCase("EXIT"));

            System.out.println("Klienti u mbyll.");
        } catch (IOException e) {
            System.out.println("Nuk mund te lidhet me serverin.");
        }
    }
}
