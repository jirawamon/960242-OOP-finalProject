package com;

import java.util.Scanner;

public class AdminMenu extends BaseMenu {

    public AdminMenu(LibraryManager manager, Scanner scanner) {
        super(manager, scanner);
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n👑 [Admin Menu] Manage Library System");
            System.out.println("1. View all members");
            System.out.println("2. Edit Member VIP Status");
            System.out.println("3. Delete member");
            System.out.println("4. Add new item (Physical/E-Book)");
            System.out.println("5. Delete item");
            System.out.println("6. Report: Unreturned Items");
            System.out.println("7. Report: Top Borrowed Items");
            System.out.println("8. Edit Item");
            System.out.println("9. Logout to Login Page");

            int choice = getIntInput("👉 Select menu (1-9): ");

            switch (choice) {
                case 1: manager.showAllMembers(); break;
                case 2: 
                    editVipStatus(); 
                    manager.showAllMembers();break;
                case 3:
                    System.out.print("Enter Member ID to delete: ");
                    if (manager.deleteMember(scanner.nextLine().trim())) System.out.println("✅ Member deleted.");
                    else System.out.println("❌ Cannot delete! Member has borrowed items or ID not found.");
                    manager.showAllMembers();
                    break;
                case 4: 
                    manager.showAllItems();
                    System.err.println("");
                    addItem(); break;
                case 5:
                    System.out.print("Enter Item ID to delete: ");
                    if (manager.deleteItem(scanner.nextLine().trim())) System.out.println("✅ Item deleted.");
                    break;
                case 6: manager.showReportUnreturned(); break;
                case 7: manager.showReportMostBorrowed(); break;
                case 8: 
                    manager.showAllItems();
                    System.err.println("");
                    updateItem(); break;
                case 9: running = false; break;
                default: System.out.println("⚠️ Please select between 1-9 only.");
            }
        }
    }

    private void editVipStatus() {
        System.out.print("Enter Member ID to edit VIP status: ");
        Member editMember = manager.findMember(scanner.nextLine().trim());

        if (editMember == null) {
            System.out.println("❌ Member ID not found.");
            return;
        }

        System.out.println("Current Status: " + (editMember.isPremium()
                ? "👑 VIP (Exp: " + editMember.getVipExpiryDate() + ")" : "👤 Regular"));
        System.out.println("1. Grant / Extend VIP (+1 Month)");
        System.out.println("2. Grant / Extend VIP (+1 Year)");
        System.out.println("3. Revoke VIP (Change to Regular)");
        System.out.println("4. Cancel");

        int action = getIntInput("👉 Choose action (1-4): ");

        if (action == 1) {
            editMember.applyVip(1);
            System.out.println("✅ VIP status updated!");
        } else if (action == 2) {
            editMember.applyVip(12);
            System.out.println("✅ VIP status updated!");
        } else if (action == 3) {
            editMember.setVipExpiryDate(null);
            System.out.println("✅ VIP status revoked. Member is now Regular.");
        } else {
            System.out.println("❌ Action canceled.");
        }
    }

    private void updateItem() {
        System.out.print("Enter Item ID to update(0 to back): ");
        String id = scanner.nextLine().trim();

        if (id.equals("0")) return;

        LibraryItem item = manager.findItem(id);

        if (item == null) {
            System.out.println("❌ Item not found!");
            return;
        }

        System.out.println("\n--- Current Item ---");
        item.displayDetails(true);

        // 🔥 Title
        System.out.print("New Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        // 🔥 Author
        System.out.print("New Author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("❌ Author cannot be empty!");
            return;
        }

        // 🔥 Price
        double price = getDoubleInput("New Price: ");
        if (price < 0) {
            System.out.println("❌ Price must be positive!");
            return;
        }

        boolean success = manager.updateItem(id, title, author, price);

        if (success) {
            System.out.println("✅ Item updated successfully!");
            manager.showAllItems();
        } else {
            System.out.println("❌ Update failed!");
        }
    }

    private void addItem() {
        int type = getIntInput("Item Type (1 = Physical Book, 2 = E-Book): ");
        if (type != 1 && type != 2) { 
            System.out.println("❌ Invalid Item Type!"); 
            return; 
        }

        // 🔥 ID validation
        String iId;
        while (true) {
            System.out.print("Item ID: ");
            iId = scanner.nextLine().trim();

            if (iId.isEmpty()) {
                System.out.println("❌ ID cannot be empty!");
                continue;
            }

            if (manager.findItem(iId) != null) {
                System.out.println("❌ This ID already exists!");
                continue;
            }

            break;
        }

        // 🔥 Title validation
        String title;
        while (true) {
            System.out.print("Title: ");
            title = scanner.nextLine().trim();

            if (title.isEmpty()) {
                System.out.println("❌ Title cannot be empty!");
                continue;
            }
            break;
        }

        // 🔥 Author validation
        String author;
        while (true) {
            System.out.print("Author: ");
            author = scanner.nextLine().trim();

            if (author.isEmpty()) {
                System.out.println("❌ Author cannot be empty!");
                continue;
            }
            break;
        }

        // 🔥 Price validation
        double price;
        while (true) {
            price = getDoubleInput("Borrow fee (THB): ");
            if (price < 0) {
                System.out.println("❌ Price must be positive!");
                continue;
            }
            break;
        }

        if (type == 1) {
            System.out.print("Location (e.g. A-12): ");
            String loc = scanner.nextLine().trim();

            if (loc.isEmpty()) {
                System.out.println("❌ Location cannot be empty!");
                return;
            }

            manager.addItem(new PhysicalBook(iId, title, author, price, loc));

        } else {
            System.out.print("Download URL: ");
            String url = scanner.nextLine().trim();

            if (url.isEmpty()) {
                System.out.println("❌ URL cannot be empty!");
                return;
            }

            double size;
            while (true) {
                size = getDoubleInput("File Size (MB): ");
                if (size <= 0) {
                    System.out.println("❌ Size must be greater than 0!");
                    continue;
                }
                break;
            }

            manager.addItem(new EBook(iId, title, author, price, url, size));
        }

        System.out.println("✅ Item added successfully!");
    }
}
