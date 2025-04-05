package com;

import java.util.List;
import java.util.Scanner;

public class User {
    private static String loggedInUserEmail = null; // ✅ Tracks logged-in user

    public static void openUserPortal(LibraryDao library, Scanner sc) {
        System.out.println("\n1. Register\n2. Login");
        System.out.print("Enter choice: ");
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input! Please enter 1 or 2.");
            sc.next(); // Clear invalid input
        }
        int choice = sc.nextInt();
        sc.nextLine(); // Consume the newline

        if (choice == 1) {
            System.out.println("Enter Registration Details:");
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            library.registerUser(name, email, password);
            System.out.println("Registered Successfully!! " + name);
        } else if (choice == 2) {
            System.out.println("Enter Login Credentials:");
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            if (library.loginUser(email, password)) {
                userActions(library, sc, email); // ✅ Pass the logged-in user's email
            } else {
                System.out.println("If you are a new member kindly register first and login!!");
            }
        } else {
            System.out.println("❌ Invalid choice! Returning to main menu.");
        }
    }


    private static void userActions(LibraryDao library, Scanner sc, String userEmail) { // ✅ Pass userEmail
        boolean flag = true;
        do {
            System.out.println("\n1. View Books\n2. Borrow Book\n3. Return Book\n4. Exit");
            System.out.print("Enter choice: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter 1, 2, 3, or 4.");
                sc.next(); // Clear invalid input
            }
            int choice = sc.nextInt();
            sc.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    System.out.println("📚 Available Books:");
                    List<Book> list = library.getAllBooks();
                    for (Book b : list) {
                        System.out.println(b);
                    }
                    break;
                case 2:
                    System.out.print("Enter book name to borrow: ");
                    String borrowBook = sc.nextLine();
                    library.borrowBook(borrowBook);
                    break;
                case 3:
                    System.out.print("Enter book name to return: ");
                    String returnBook = sc.nextLine();
                    library.returnBook(returnBook, userEmail); // ✅ Pass userEmail for verification
                    break;
                case 4:
                    System.out.println("📌 Exiting User Portal.");
                    flag = false;
                    break;
                default:
                    System.out.println("❌ Invalid choice! Please enter a valid option.");
            }
        } while (flag);
    }

    
}
