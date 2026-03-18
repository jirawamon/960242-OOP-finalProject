package com;

import java.util.Scanner;

public class UserMenu extends BaseMenu {
    private final Member user;

    public UserMenu(LibraryManager manager, Scanner scanner, Member user) {
        super(manager, scanner);
        this.user = user;
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n👤 Welcome, " + user.getName() + " | Wallet: ฿" + user.getBalance());
            System.out.println("1. View all items");
            System.out.println("2. Borrow a book");
            System.out.println("3. Return a book(s)");
            System.out.println("4. Top up Wallet");
            System.out.println("5. My Profile & Borrowed Books");
            System.out.println("6. Upgrade / Renew VIP");
            System.out.println("7. Logout to Main Menu");

            int choice = getIntInput("👉 Select menu (1-7): ");

            switch (choice) {
                case 1: manager.showAllItems(); break;
                case 2: borrowBook(); break;
                case 3: returnBooks(); break;
                case 4:
                    double topup;

                    while (true) {
                        topup = getDoubleInput("Amount to top up (THB): ");

                        if (topup <= 0) {
                            System.out.println("❌ Amount must be greater than 0!");
                            continue;
                        }

                        break;
                    }

                    user.addBalance(topup);
                    System.out.println("✅ Top-up successful!");
                    System.out.println("💰 Current balance: ฿" + user.getBalance());
                case 5:
                    user.displayMember();
                    System.out.println("📚 Your Borrowed Books:");
                    user.showBorrowedBooks();
                    break;
                case 6: upgradeVip(); break;
                case 7: running = false; break;
                default: System.out.println("⚠️ Please select between 1-7 only.");
            }
        }
    }

    private void borrowBook() {
        System.out.print("Enter Book ID to borrow: ");
        String id = scanner.nextLine().trim();

        if (id.isEmpty()) {
            System.out.println("❌ Book ID cannot be empty!");
            return;
        }

        LibraryItem item = manager.findItem(id);

        if (item == null) {
            System.out.println("❌ Book ID not found.");
            return;
        }

        if (!item.isAvailable()) {
            System.out.println("❌ This book is already borrowed.");
            return;
        }

        if (item.borrowItem(user)) {
            System.out.println("✅ Borrowed successfully! Charged ฿" + item.getPrice());
            System.out.println("📅 Due date: " + item.getDueDate());
        }
    }

    private void returnBooks() {
        if (user.getBorrowedItems().isEmpty()) {
            System.out.println("✅ You have no books to return.");
            return;
        }

        System.out.println("\n📚 Your Borrowed Books:");
        user.showBorrowedBooks();

        System.out.print("\nEnter Book ID(s) to return (e.g., B01 E01): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("❌ Input cannot be empty!");
            return;
        }

        String[] bookIds = input.split("[,\\s]+");

        for (String id : bookIds) {
            LibraryItem item = manager.findItem(id);
            System.out.println("\n🔄 Processing: " + id);

            if (item != null && !item.isAvailable() && item.getBorrowedBy() == user) {

                int lateDays;
                while (true) {
                    lateDays = getIntInput("How many days late? (0 if on time): ");
                    if (lateDays < 0) {
                        System.out.println("❌ Late days cannot be negative!");
                        continue;
                    }
                    break;
                }

                double fine = item.calculateFine(lateDays);

                if (fine > 0) {
                    if (!user.deductBalance(fine)) {
                        System.out.println("❌ Not enough balance to pay fine!");
                        continue;
                    }
                    System.out.println("💸 Fine paid: ฿" + fine);
                }

                item.returnItem();
                System.out.println("✅ Book returned successfully!");

            } else {
                System.out.println("❌ You didn't borrow this book or ID is incorrect.");
            }
        }
    }

    private void upgradeVip() {
        System.out.println("\n--- 🌟 Upgrade / Renew VIP ---");
        System.out.println("Current status: " + (user.isPremium()
                ? "👑 VIP (Exp: " + user.getVipExpiryDate() + ")" : "👤 Regular"));

        int choice;
        while (true) {
            System.out.println("1. 1 Month Plan (150 THB)");
            System.out.println("2. 1 Year Plan (1,500 THB)");
            System.out.println("3. Cancel");

            choice = getIntInput("👉 Choose plan (1-3): ");

            if (choice < 1 || choice > 3) {
                System.out.println("❌ Invalid choice! Please select 1-3.");
                continue;
            }
            break;
        }

        if (choice == 3) {
            System.out.println("↩️ Cancelled.");
            return;
        }

        double cost = (choice == 1) ? 150 : 1500;

        // 🔥 เช็คเงิน
        if (!user.deductBalance(cost)) {
            System.out.println("❌ Insufficient Wallet balance! Please top up first.");
            return;
        }

        // 🔥 อัป VIP
        user.applyVip(choice == 1 ? 1 : 12);

        System.out.println("✅ VIP upgraded successfully!");
        System.out.println("💰 Remaining balance: ฿" + user.getBalance());
        System.out.println("📅 New expiry: " + user.getVipExpiryDate());
    }
}
