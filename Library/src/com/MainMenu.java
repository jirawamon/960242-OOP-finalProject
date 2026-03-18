package com;

import java.util.Scanner;

public class MainMenu extends BaseMenu {

    public MainMenu(LibraryManager manager, Scanner scanner) {
        super(manager, scanner);
    }

    

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n===============================================");
            System.out.println(" 🌟 Smart Library System (Auto-VIP System) 🌟");
            System.out.println("===============================================");
            System.out.println("1. Login (เข้าสู่ระบบ)");
            System.out.println("2. Register (สมัครสมาชิกใหม่)");
            System.out.println("3. Exit (ออกจากระบบ)");

            int choice = getIntInput("👉 Select menu (1-3): ");

            if (choice == 1) {
                login();
            } else if (choice == 2) {
                register();
            } else if (choice == 3) {
                manager.saveData();
                System.out.println("💾 Data saved. Goodbye!");
                running = false;
            } else {
                System.out.println("⚠️ Please select between 1-3 only.");
            }
        }
    }

    private void login() {
        while (true) {
            System.out.println("\n--- 🔐 Login System ---");
            System.out.print("👉 Enter Member ID (Type 'admin' or 0 to back): ");

            String loginId = scanner.nextLine().trim();

            // ✅ กด 0 = กลับ
            if (loginId.equals("0")) {
                return;
            }

            if (loginId.equalsIgnoreCase("admin")) {
                new AdminMenu(manager, scanner).run();
                return;
            }

            Member user = manager.findMember(loginId);
            if (user != null) {
                new UserMenu(manager, scanner, user).run();
                return;
            } else {
                System.out.println("❌ User ID not found! Try again.");
            }
        }
    }

    private void register() {
        while (true) {
            System.out.println("\n--- 📝 User Registration ---");
            System.out.print("Set your Member ID (or 0 to back): ");

            String mId = scanner.nextLine().trim();

            // ✅ กด 0 = กลับ
            if (mId.equals("0")) {
                return;
            }

            if (mId.isEmpty()) {
                System.out.println("❌ ID cannot be empty!");
                continue;
            }

            if (manager.findMember(mId) != null) {
                System.out.println("❌ ID already exists! Try again.");
                continue;
            }

            System.out.print("Your Name: ");
            String mName = scanner.nextLine().trim();

            manager.addMember(new Member(mId, mName, 0.0, 0, null));

            System.out.println("✅ Registration successful!");
            System.out.println("💡 Please login to continue.");
            return;
        }
    }
}
