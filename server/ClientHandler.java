package server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private Socket socket;
	private LibraryManager libraryManager;

	public ClientHandler(Socket socket, LibraryManager libraryManager) {
		this.socket = socket;
		this.libraryManager = libraryManager;
	}

	@Override
	public void run() {
		try (
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
		) {
			String request;
			while ((request = input.readLine()) != null) {
				String response = processRequest(request);
				output.println(response);
			}
		} catch (IOException e) {
			System.out.println("Lidhja me klientin u ndërpre.");
		}
	}

	private String processRequest(String request) {
		String[] parts = request.split(";", 2);
		String command = parts[0].toUpperCase();
		String argument = parts.length > 1 ? parts[1] : "";

		switch (command) {
			case "KERKO":
				return libraryManager.searchBooks(argument);
			case "HUAZO":
				return libraryManager.borrowBook(argument);
			case "KTHE":
				return libraryManager.returnBook(argument);
			case "LISTO":
				return libraryManager.listBooks();
			default:
				return "Komandë e panjohur.";
		}
	}
}
