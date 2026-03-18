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
        System.out.println("\n--- 🔐 Login System ---");
        System.out.print("👉 Enter Member ID (Type 'admin' for Admin): ");
        String loginId = scanner.nextLine().trim();

        if (loginId.equalsIgnoreCase("admin")) {
            new AdminMenu(manager, scanner).run();
        } else {
            Member user = manager.findMember(loginId);
            if (user != null) {
                new UserMenu(manager, scanner, user).run();
            } else {
                System.out.println("❌ User ID not found! Please register first.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- 📝 User Registration ---");
        System.out.print("Set your Member ID (e.g. M03): ");
        String mId = scanner.nextLine().trim();

        if (manager.findMember(mId) != null) {
            System.out.println("❌ ID already exists! Please use another ID.");
            return;
        }

        System.out.print("Your Name: ");
        String mName = scanner.nextLine().trim();

        manager.addMember(new Member(mId, mName, 0.0, 0, null));
        System.out.println("✅ Registration successful!");
        System.out.println("💡 You are now a Regular member. Please login to top up your wallet and upgrade to VIP.");
    }
}
