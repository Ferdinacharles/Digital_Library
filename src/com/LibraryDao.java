
package com;
import java.util.List;

public interface LibraryDao {
    int addNewBooks(String name, String author, float price, int count);
    int removeBooks(String name, int count);
    List<Book> getAllBooks();
    boolean registerUser(String name, String email, String password);
    boolean loginUser(String email, String password);
    boolean borrowBook(String name);
    boolean returnBook(String bookName, String userEmail);

}
