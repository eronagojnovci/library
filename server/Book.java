package server;

public class Book {
    private String id;
    private String title;
    private String author;
    private int available;

    public Book(String id, String title, String author, int available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Titulli: %s | Autori: %s | Në dispozicion: %d", id, title, author, available);
    }
}
