package server;

import java.util.*;

public class LibraryManager {
    private final Map<String, Book> books = new HashMap<>();

    public LibraryManager() {
        addBook(new Book("1", "Java per fillestare", "Autor A", 3 , "/assets/java.jpg"));
        addBook(new Book("2", "Struktura e te dhenave", "Autor B", 2, "/assets/struktura.jpeg"));
        addBook(new Book("3", "Algoritme ne Java", "Autor C", 1, "/assets/algoritme.jpg"));
        addBook(new Book("4", "Programim i distibuar", "Autor C", 2,"/assets/distrubuted.jpeg"));
        addBook(new Book("5", "Kalkulus", "Autor D", 2,"/assets/kalkulus.jpg"));
        addBook(new Book("6", "Algjeber", "Autor D", 2,"/assets/algjeber.jpg"));
        addBook(new Book("7", "Analize e te dhenave", "Autor D", 2,"/assets/analize.jpg"));
        addBook(new Book("8", "Java per fillestare", "Autor A", 5 , "/assets/java.jpg"));
        addBook(new Book("9", "Kalkulus 2", "Autor D", 2,"/assets/kalkulus.jpg"));
        addBook(new Book("10", "Algjeber 2", "Autor D", 2,"/assets/algjeber.jpg"));


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
