package server;

import shared.LibraryService;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class LibraryServiceImpl extends UnicastRemoteObject implements LibraryService {
    private final LibraryManager libraryManager;

    public LibraryServiceImpl() throws RemoteException {
        super();
        this.libraryManager = new LibraryManager();
    }

    @Override
    public String searchBooks(String keyword) throws RemoteException {
        return libraryManager.searchBooks(keyword);
    }

    @Override
    public String borrowBook(String bookId) throws RemoteException {
        return libraryManager.borrowBook(bookId);
    }

    @Override
    public String returnBook(String bookId) throws RemoteException {
        return libraryManager.returnBook(bookId);
    }

    @Override
    public String listBooks() throws RemoteException {
        return libraryManager.listBooks();
    }
}
