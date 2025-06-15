package server;

public class Book {
    private final String id;
    private final String title;
    private final String author;
    private int available;
    private final String coverPhotoPath;

    public Book(String id, String title, String author, int available, String coverPhotoPath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.coverPhotoPath = coverPhotoPath;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getAvailable() { return available; }
    public String getCoverPhotoPath() { return coverPhotoPath; }

    public void setAvailable(int available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("ID:%s|Titulli:%s|Autori:%s|Në dispozicion:%d|Foto:%s",
                id, title, author, available, coverPhotoPath);
    }
}
