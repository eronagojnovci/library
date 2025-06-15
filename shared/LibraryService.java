// LibraryService.java
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LibraryService extends Remote {
    String searchBooks(String keyword) throws RemoteException;
    String borrowBook(String bookId) throws RemoteException;
    String returnBook(String bookId) throws RemoteException;
    String listBooks() throws RemoteException;
}
