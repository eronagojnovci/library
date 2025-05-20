package server;

import java.util.*;

public class LibraryManager {
    private final Map<String, Book> books = new HashMap<>();

    public LibraryManager() {
        // Shtojmë disa libra fillestarë
        addBook(new Book("1", "Java per fillestare", "Autor A", 3));
        addBook(new Book("2", "Struktura te te dhenave", "Autor B", 2));
        addBook(new Book("3", "Algoritme ne Java", "Autor C", 1));
    }

    public synchronized void addBook(Book book) {
        books.put(book.getId(), book);
    }

    public synchronized String searchBooks(String keyword) {
        keyword = keyword.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (Book b : books.values()) {
            if (b.getId().toLowerCase().contains(keyword) ||
                    b.getTitle().toLowerCase().contains(keyword) ||
                    b.getAuthor().toLowerCase().contains(keyword)) {
                sb.append(b).append("\n");
            }
        }
        return sb.length() == 0 ? "Nuk u gjet asnjë libër." : sb.toString();
    }


    public synchronized String borrowBook(String bookId) {
        Book b = books.get(bookId);
        if (b == null) return "Libri nuk ekziston.";
        if (b.getAvailable() > 0) {
            b.setAvailable(b.getAvailable() - 1);
            return "Libri '" + b.getTitle() + "' u huazua me sukses.";
        } else {
            return "Libri nuk është në dispozicion për huazim.";
        }
    }

    public synchronized String returnBook(String bookId) {
        Book b = books.get(bookId);
        if (b == null) return "Libri nuk ekziston.";
        b.setAvailable(b.getAvailable() + 1);
        return "Libri '" + b.getTitle() + "' u kthye me sukses.";
    }

    public synchronized String listBooks() {
        StringBuilder sb = new StringBuilder("Lista e librave:\n");
        for (Book b : books.values()) {
            sb.append(b).append("\n");
        }
        return sb.toString();
    }
}
