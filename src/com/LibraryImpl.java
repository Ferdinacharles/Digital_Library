package com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibraryImpl implements LibraryDao {
    @Override
    public int addNewBooks(String name, String author, float price, int count) {
        String query = "INSERT INTO books (book_name, book_author, book_price, book_count) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, author);
            ps.setFloat(3, price);
            ps.setInt(4, count);
            return ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public int removeBooks(String name, int count) {
        String checkQuery = "SELECT book_count FROM books WHERE book_name = ?";
        String updateQuery = "UPDATE books SET book_count = ? WHERE book_name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkQuery)) {

            checkPs.setString(1, name);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int availableCount = rs.getInt("book_count");

                if (availableCount == 0) {
                    System.out.println("❌ No copies of '" + name + "' are available to remove.");
                    return 0;
                }

                if (count > availableCount) {
                    System.out.println("❌ Only " + availableCount + " books are available. Cannot remove " + count + ".");
                    return 0;
                }

                int newCount = availableCount - count; // 📌 Reduce the count but do NOT delete

                try (PreparedStatement updatePs = con.prepareStatement(updateQuery)) {
                    updatePs.setInt(1, newCount);
                    updatePs.setString(2, name);
                    System.out.println("Book removed succesfully");
                    return updatePs.executeUpdate();
                }
            } else {
                System.out.println("❌ Book not found in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }




    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                books.add(new Book(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getFloat(4), rs.getInt(5)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return books;
    }

    @Override
    public boolean registerUser(String name, String email, String password) {
        String checkQuery = "SELECT * FROM users WHERE name = ? AND email = ? AND password = ?";
        String insertQuery = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkQuery)) {

            checkPs.setString(1, name);
            checkPs.setString(2, email);
            checkPs.setString(3, password);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {  // ✅ If a record exists with the same details
                System.out.println("❌ User is already registered with the same details.");
                return false;
            }

            try (PreparedStatement insertPs = con.prepareStatement(insertQuery)) {
                insertPs.setString(1, name);
                insertPs.setString(2, email);
                insertPs.setString(3, password);
                return insertPs.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean loginUser(String email, String password) {
        // ✅ Edge Case 1: Check for empty email or password
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("❌ Email or password cannot be empty.");
            return false;
        }

        // ✅ Edge Case 2: Ensure email exists before checking the password
        String emailCheckQuery = "SELECT password FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement emailPs = con.prepareStatement(emailCheckQuery)) {
            
            emailPs.setString(1, email);
            ResultSet rs = emailPs.executeQuery();

            if (!rs.next()) {  // 📌 If no user exists with this email
                System.out.println("❌ No account found with this email.");
                return false;
            }

            // ✅ Edge Case 3: Validate password
            String storedPassword = rs.getString("password");
            if (!storedPassword.equals(password)) {
                System.out.println("❌ Incorrect password.");
                return false;
            }

            System.out.println("✅ Login successful! Welcome.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean borrowBook(String name) {
        // ✅ Edge Case 1: Check for empty book name
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Book name cannot be empty.");
            return false;
        }

        // ✅ Edge Case 2: Check if the book exists and has available copies
        String checkQuery = "SELECT book_count FROM books WHERE book_name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkQuery)) {
            
            checkPs.setString(1, name);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {  // 📌 Book does not exist in the database
                System.out.println("❌ Book not found.");
                return false;
            }

            int availableCount = rs.getInt("book_count");
            if (availableCount <= 0) {  // 📌 No copies available
                System.out.println("❌ Sorry, this book is currently unavailable.");
                return false;
            }

            // ✅ Borrow the book (remove one copy)
            return removeBooks(name, 1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean returnBook(String name, String userEmail) {  // 📌 Takes userEmail to validate borrowing
        // ✅ Edge Case 1: Check for empty book name
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Book name cannot be empty.");
            return false;
        }

        // ✅ Edge Case 2: Check if the book exists in the library
        String checkBookQuery = "SELECT book_count FROM books WHERE book_name = ?";
        String checkBorrowedQuery = "SELECT COUNT(*) FROM transactions WHERE email = ? AND book_name = ? AND status = 'BORROWED'";
        String updateTransactionQuery = "UPDATE transactions SET status = 'RETURNED' WHERE email = ? AND book_name = ? AND status = 'BORROWED' LIMIT 1";
        String updateBookQuery = "UPDATE books SET book_count = book_count + 1 WHERE book_name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkBookPs = con.prepareStatement(checkBookQuery)) {

            checkBookPs.setString(1, name);
            ResultSet bookRs = checkBookPs.executeQuery();

            if (!bookRs.next()) {  
                // ❌ Book does not exist in library
                System.out.println("❌ This book is not from our library.");
                return false;
            }

            // ✅ Check if the user actually borrowed the book
            try (PreparedStatement checkBorrowedPs = con.prepareStatement(checkBorrowedQuery)) {
                checkBorrowedPs.setString(1, userEmail);
                checkBorrowedPs.setString(2, name);
                ResultSet rs = checkBorrowedPs.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {  
                    // ✅ User borrowed the book, proceed with return
                    try (PreparedStatement updateTransactionPs = con.prepareStatement(updateTransactionQuery);
                         PreparedStatement updateBookPs = con.prepareStatement(updateBookQuery)) {

                        // Update transaction status to "RETURNED"
                        updateTransactionPs.setString(1, userEmail);
                        updateTransactionPs.setString(2, name);
                        updateTransactionPs.executeUpdate();

                        // Increase book count in the library
                        updateBookPs.setString(1, name);
                        return updateBookPs.executeUpdate() > 0;
                    }
                } else {
                    System.out.println("❌ You didn't borrow this book from our library.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }






}
