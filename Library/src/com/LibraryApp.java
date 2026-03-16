package com;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) throws Exception {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        LibraryManager manager = new LibraryManager();
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        boolean running = true;

        System.out.println("===========================================");
        System.out.println(" 🌟 Smart Library System (VIP & Wallet) 🌟");
        System.out.println("===========================================");

        while (running) {

            // Show book status on every loop
            System.out.println("\n📚 Current Book Status");
            manager.showAllItems();

            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View all members");
            System.out.println("2. Register new member (VIP available)");
            System.out.println("3. Top up Wallet");
            System.out.println("4. Borrow a book (instant charge & due date)");
            System.out.println("5. Return a book (late fine calculation)");
            System.out.println("6. Exit (save data)");
            System.out.print("👉 Select menu: ");

            try {

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {

                    case 1:
                        manager.showAllMembers();
                        break;

                    case 2:
                        System.out.print("Set member ID (e.g. M03): ");
                        String mId = scanner.nextLine();

                        if (manager.findMember(mId) != null) {
                            System.out.println("❌ Cannot register: ID already exists!");
                            break;
                        }

                        System.out.print("Member name: ");
                        String mName = scanner.nextLine();

                        System.out.print("Initial deposit (THB): ");
                        double initialMoney = scanner.nextDouble();

                        System.out.println("Select membership type:");
                        System.out.println("1. Regular member (borrow limit: 3 books)");
                        System.out.println("2. VIP member (unlimited borrowing!)");
                        System.out.print("Choose (1 or 2): ");

                        int type = scanner.nextInt();
                        boolean isVip = (type == 2);

                        manager.addMember(new Member(mId, mName, initialMoney, 0, isVip));

                        System.out.println("✅ Registration successful!");
                        break;

                    case 3:
                        System.out.print("Enter member ID: ");
                        Member mTopup = manager.findMember(scanner.nextLine());

                        if (mTopup != null) {

                            System.out.print("Amount to top up: ");
                            mTopup.addBalance(scanner.nextDouble());

                        } else {

                            System.out.println("❌ Member ID not found");

                        }
                        break;

                    case 4:

                        System.out.print("Book ID to borrow: ");
                        LibraryItem itemToBorrow = manager.findItem(scanner.nextLine());

                        if (itemToBorrow != null) {

                            System.out.print("Borrower member ID: ");
                            Member mBorrow = manager.findMember(scanner.nextLine());

                            if (mBorrow != null) {

                                if (itemToBorrow.borrowItem(mBorrow)) {

                                    System.out.println("✅ Borrowed successfully! Charged ฿" + itemToBorrow.getPrice());
                                    System.out.println("📅 Due date: " + itemToBorrow.getDueDate());

                                }

                            } else {

                                System.out.println("❌ Member ID not found");

                            }

                        } else {

                            System.out.println("❌ Book ID not found");

                        }

                        break;

                    case 5:

                        System.out.print("Book ID to return: ");
                        LibraryItem itemToReturn = manager.findItem(scanner.nextLine());

                        if (itemToReturn != null && !itemToReturn.isAvailable()) {

                            Member borrower = itemToReturn.getBorrowedBy();

                            System.out.print("How many days late? (0 if on time): ");
                            int lateDays = scanner.nextInt();

                            double fine = itemToReturn.calculateFine(lateDays);

                            if (fine > 0) {
                                borrower.payFine(fine);
                            }

                            itemToReturn.returnItem();

                            System.out.println("✅ Book returned successfully!");

                        } else {

                            System.out.println("❌ Book is already available or ID is incorrect");

                        }

                        break;

                    case 6:

                        manager.saveData();
                        System.out.println("💾 Data saved. Thank you!");
                        running = false;

                        break;

                    default:

                        System.out.println("⚠️ Please select 1-6 only");

                }

            } catch (Exception e) {

                System.out.println("❌ Invalid input, please try again!");
                scanner.nextLine();

            }

        }

        scanner.close();
    }
}