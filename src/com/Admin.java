package com;

import java.util.List;
import java.util.Scanner;

public class Admin {
	static Scanner sc = new Scanner(System.in);
    public static void openAdminPortal(LibraryDao library) {
        boolean flag = true;
        do {
            System.out.println("1. Add New Books\n2. Remove Books\n3. View All Books\n4. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume the leftover newline

            switch (choice) {
                case 1:
                    System.out.println("Enter book details: ");
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Author: ");
                    String author = sc.nextLine();
                    System.out.print("Price: ");
                    while (!sc.hasNextFloat()) {  // Ensure valid float input
                        System.out.println("Invalid input. Enter a valid price:");
                        sc.next();
                    }
                    float price = sc.nextFloat();
                    System.out.print("Count: ");
                    while (!sc.hasNextInt()) {  // Ensure valid integer input
                        System.out.println("Invalid input. Enter a valid count:");
                        sc.next();
                    }
                    int count = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    library.addNewBooks(name, author, price, count);
                    System.out.println("Books added succesfully!!");
                    break;
                case 2:
                    System.out.print("Enter book name to remove: ");
                    String removeBookName = sc.nextLine();
                    System.out.print("Enter count to remove: ");
                    while (!sc.hasNextInt()) {
                        System.out.println("Invalid input. Enter a valid count:");
                        sc.next();
                    }
                    int removeCount = sc.nextInt();
                    sc.nextLine(); // Consume newline

                    library.removeBooks(removeBookName, removeCount);
                    
                    break;
                case 3:
                    List<Book> list =library.getAllBooks();
                    for(Book b: list) {
                    	System.out.println(b);
                    }
                    break;
                case 4:
                	System.out.println("📌 Exiting Admin Portal.");
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (flag);
    }
}
