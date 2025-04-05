package com;

import java.util.Scanner;

public class Driver {
	public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LibraryDao library = new LibraryImpl();
        boolean flag = true;

        while (flag) {
            System.out.println("1. Admin Portal\n2. User Portal\n3. Exit");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    Admin.openAdminPortal(library);
                    break;
                case 2:
                    User.openUserPortal(library, sc);
                    break;
                case 3:
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        sc.close();
    }
}
