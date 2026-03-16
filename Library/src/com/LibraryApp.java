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

        while (running) {
            System.out.println("\n===============================================");
            System.out.println(" 🌟 Smart Library System (Auto-VIP System) 🌟");
            System.out.println("===============================================");
            System.out.println("1. Login (เข้าสู่ระบบ)");
            System.out.println("2. Register (สมัครสมาชิกใหม่)");
            System.out.println("3. Exit (ออกจากระบบ)");

            int startMenu = getIntInput(scanner, "👉 Select menu (1-3): ");

            if (startMenu == 1) {
                System.out.println("\n--- 🔐 Login System ---");
                System.out.print("👉 Enter Member ID (Type 'admin' for Admin): ");
                String loginId = scanner.nextLine().trim();

                if (loginId.equalsIgnoreCase("admin")) {
                    runAdminMenu(manager, scanner);
                } else {
                    Member user = manager.findMember(loginId);
                    if (user != null) {
                        runUserMenu(manager, scanner, user);
                    } else {
                        System.out.println("❌ User ID not found! Please register first.");
                    }
                }
            } else if (startMenu == 2) {
                System.out.println("\n--- 📝 User Registration ---");
                System.out.print("Set your Member ID (e.g. M03): ");
                String mId = scanner.nextLine().trim();

                if (manager.findMember(mId) != null) {
                    System.out.println("❌ ID already exists! Please use another ID.");
                    continue;
                }

                System.out.print("Your Name: ");
                String mName = scanner.nextLine().trim();

                // 📌 ปรับให้สมัครเป็นสมาชิกธรรมดา (Regular) เงิน 0 บาท และยังไม่มีวันหมดอายุ VIP
                manager.addMember(new Member(mId, mName, 0.0, 0, null));
                System.out.println("✅ Registration successful!");
                System.out.println("💡 You are now a Regular member. Please login to top up your wallet and upgrade to VIP.");

            } else if (startMenu == 3) {
                manager.saveData();
                System.out.println("💾 Data saved. Goodbye!");
                running = false;
            } else {
                System.out.println("⚠️ Please select between 1-3 only.");
            }
        }
        scanner.close();
    }

    // ---------------------------------------------------------
    // 👑 Admin Menu
    // ---------------------------------------------------------
    private static void runAdminMenu(LibraryManager manager, Scanner scanner) {
        boolean adminRunning = true;
        while (adminRunning) {
            System.out.println("\n👑 [Admin Menu] Manage Library System");
            System.out.println("1. View all members");
            System.out.println("2. Edit Member VIP Status");
            System.out.println("3. Delete member");
            System.out.println("4. Add new item (Physical/E-Book)");
            System.out.println("5. Delete item");
            System.out.println("6. Report: Unreturned Items");
            System.out.println("7. Report: Top Borrowed Items");
            System.out.println("8. Logout to Login Page");

            int choice = getIntInput(scanner, "👉 Select menu (1-8): ");

            switch (choice) {
                case 1: manager.showAllMembers(); break;
                case 2:
                    System.out.print("Enter Member ID to edit VIP status: ");
                    Member editMember = manager.findMember(scanner.nextLine().trim());

                    if (editMember != null) {
                        System.out.println("Current Status: " + (editMember.isPremium() ? "👑 VIP (Exp: " + editMember.getVipExpiryDate() + ")" : "👤 Regular"));
                        System.out.println("1. Grant / Extend VIP (+1 Month)");
                        System.out.println("2. Grant / Extend VIP (+1 Year)");
                        System.out.println("3. Revoke VIP (Change to Regular)");
                        System.out.println("4. Cancel");

                        int action = getIntInput(scanner, "👉 Choose action (1-4): ");

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
                    } else {
                        System.out.println("❌ Member ID not found.");
                    }
                    break;
                case 3:
                    System.out.print("Enter Member ID to delete: ");
                    if (manager.deleteMember(scanner.nextLine().trim())) System.out.println("✅ Member deleted.");
                    break;
                case 4:
                    int type = getIntInput(scanner, "Item Type (1 = Physical Book, 2 = E-Book): ");
                    if (type != 1 && type != 2) { System.out.println("❌ Invalid Item Type!"); break; }

                    System.out.print("Item ID: "); String iId = scanner.nextLine().trim();
                    System.out.print("Title: "); String title = scanner.nextLine().trim();
                    System.out.print("Author: "); String author = scanner.nextLine().trim();
                    double price = getDoubleInput(scanner, "Borrow fee (THB): ");

                    if (type == 1) {
                        System.out.print("Location (e.g. A-12): ");
                        manager.addItem(new PhysicalBook(iId, title, author, price, scanner.nextLine().trim()));
                    } else {
                        System.out.print("Download URL: "); String url = scanner.nextLine().trim();
                        double size = getDoubleInput(scanner, "File Size (MB): ");
                        manager.addItem(new EBook(iId, title, author, price, url, size));
                    }
                    System.out.println("✅ Item added successfully!");
                    break;
                case 5:
                    System.out.print("Enter Item ID to delete: ");
                    if (manager.deleteItem(scanner.nextLine().trim())) System.out.println("✅ Item deleted.");
                    break;
                case 6: manager.showReportUnreturned(); break;
                case 7: manager.showReportMostBorrowed(); break;
                case 8: adminRunning = false; break;
                default: System.out.println("⚠️ Please select between 1-8 only.");
            }
        }
    }

    // ---------------------------------------------------------
    // 👤 User Menu
    // ---------------------------------------------------------
    private static void runUserMenu(LibraryManager manager, Scanner scanner, Member user) {
        boolean userRunning = true;
        while (userRunning) {
            System.out.println("\n👤 Welcome, " + user.getName() + " | Wallet: ฿" + user.getBalance());
            System.out.println("1. View all items");
            System.out.println("2. Borrow a book");
            System.out.println("3. Return a book(s)");
            System.out.println("4. Top up Wallet");
            System.out.println("5. My Profile & Borrowed Books");
            System.out.println("6. Upgrade / Renew VIP");
            System.out.println("7. Logout to Main Menu");

            int choice = getIntInput(scanner, "👉 Select menu (1-7): ");

            switch (choice) {
                case 1: manager.showAllItems(); break;
                case 2:
                    System.out.print("Enter Book ID to borrow: ");
                    LibraryItem itemToBorrow = manager.findItem(scanner.nextLine().trim());
                    if (itemToBorrow != null) {
                        if (itemToBorrow.borrowItem(user)) {
                            System.out.println("✅ Borrowed successfully! Charged ฿" + itemToBorrow.getPrice());
                            System.out.println("📅 Due date: " + itemToBorrow.getDueDate());
                        }
                    } else {
                        System.out.println("❌ Book ID not found.");
                    }
                    break;
                case 3:
                    if (user.getBorrowedItems().isEmpty()) {
                        System.out.println("✅ You have no books to return.");
                        break;
                    }
                    System.out.println("\n📚 Your Borrowed Books:");
                    user.showBorrowedBooks();

                    System.out.print("\nEnter Book ID(s) to return (separated by space, e.g., B01 E01): ");
                    String[] bookIds = scanner.nextLine().trim().split("[,\\s]+");

                    for (String id : bookIds) {
                        LibraryItem itemToReturn = manager.findItem(id);
                        System.out.println("\n🔄 Processing: " + id);

                        if (itemToReturn != null && !itemToReturn.isAvailable() && itemToReturn.getBorrowedBy() == user) {
                            int lateDays = getIntInput(scanner, "How many days late? (0 if on time): ");
                            double fine = itemToReturn.calculateFine(lateDays);

                            if (fine > 0) user.payFine(fine);
                            itemToReturn.returnItem();
                            System.out.println("✅ Book returned successfully!");
                        } else {
                            System.out.println("❌ You didn't borrow this book or ID is incorrect.");
                        }
                    }
                    break;
                case 4:
                    double topup = getDoubleInput(scanner, "Amount to top up (THB): ");
                    if (topup > 0) user.addBalance(topup);
                    break;
                case 5:
                    user.displayMember();
                    System.out.println("📚 Your Borrowed Books:");
                    user.showBorrowedBooks();
                    break;
                case 6:
                    System.out.println("\n--- 🌟 Upgrade / Renew VIP ---");
                    System.out.println("Current status: " + (user.isPremium() ? "👑 VIP (Exp: " + user.getVipExpiryDate() + ")" : "👤 Regular"));
                    System.out.println("1. 1 Month Plan (150 THB)");
                    System.out.println("2. 1 Year Plan (1,500 THB)");
                    System.out.println("3. Cancel");

                    int renewChoice = getIntInput(scanner, "👉 Choose plan (1-3): ");
                    double cost = (renewChoice == 1) ? 150 : (renewChoice == 2 ? 1500 : 0);

                    // 📌 ระบบจะตรวจสอบเงินใน Wallet ถ้าไม่พอ จะซื้อ VIP ไม่ได้ (ตามที่เขียนไว้ในโค้ดเดิม)
                    if (cost > 0) {
                        if (user.deductBalance(cost)) {
                            user.applyVip(renewChoice == 1 ? 1 : 12);
                        } else {
                            System.out.println("❌ Insufficient Wallet balance! Please top up first.");
                        }
                    }
                    break;
                case 7:
                    userRunning = false;
                    break;
                default:
                    System.out.println("⚠️ Please select between 1-7 only.");
            }
        }
    }

    // =========================================================
    // 🛡️ Helper Methods for Input Validation
    // =========================================================

    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Please enter a valid integer number!");
            }
        }
    }

    private static double getDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Please enter a valid decimal number!");
            }
        }
    }
}