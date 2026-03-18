package com;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member {
    private final String id;
    private final String name;
    private double balance;
    private int borrowedCount;
    private LocalDate vipExpiryDate;
    private static final int BORROW_LIMIT = 3;
    private final List<LibraryItem> borrowedItems;

    public Member(String id, String name, double balance, int borrowedCount, LocalDate vipExpiryDate) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.borrowedCount = borrowedCount;
        this.vipExpiryDate = vipExpiryDate;
        this.borrowedItems = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public int getBorrowedCount() { return borrowedCount; }
    public LocalDate getVipExpiryDate() { return vipExpiryDate; }
    public List<LibraryItem> getBorrowedItems() { return Collections.unmodifiableList(borrowedItems); }

    // 📌 เพิ่ม Method สำหรับ Admin เพื่อแก้ไข/ยกเลิกวันหมดอายุโดยตรง
    public void setVipExpiryDate(LocalDate vipExpiryDate) {
        this.vipExpiryDate = vipExpiryDate;
    }

    public boolean isPremium() {
        if (vipExpiryDate == null) return false;
        return !LocalDate.now().isAfter(vipExpiryDate);
    }

    public void applyVip(int months) {
        if (isPremium()) {
            this.vipExpiryDate = this.vipExpiryDate.plusMonths(months);
        } else {
            this.vipExpiryDate = LocalDate.now().plusMonths(months);
        }
        System.out.println("🎉 VIP applied! New expiry date: " + this.vipExpiryDate);
    }

    public void addBalance(double amount) {
        this.balance += amount;
        System.out.println("💰 Top-up successful! Balance: ฿" + this.balance);
    }

    public boolean deductBalance(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public void payFine(double amount) {
        this.balance -= amount;
        System.out.println("💸 Late fine deducted: ฿" + amount + " | Balance: ฿" + this.balance);
    }

    public void addBorrowedItem(LibraryItem item) {
        if (!borrowedItems.contains(item)) borrowedItems.add(item);
    }

    public void removeBorrowedItem(LibraryItem item) {
        borrowedItems.remove(item);
    }

    public boolean canBorrow() {
        if (isPremium()) return true;
        return borrowedCount < BORROW_LIMIT;
    }

    public void recordBorrow() { borrowedCount++; }
    public void recordReturn() { if (borrowedCount > 0) borrowedCount--; }

    public void showBorrowedBooks() {
        if (borrowedItems.isEmpty()) {
            System.out.println("- No books currently borrowed -");
            return;
        }
        for (LibraryItem item : borrowedItems) {
            System.out.printf("   📖 ID: %s | Title: %s | Due: %s\n",
                    item.getId(), item.getTitle(), item.getDueDate());
        }
    }

    public void displayMember() {
        String memberType = isPremium() ? "👑 [VIP]" : "👤 [Regular]";
        String limitText = isPremium() ? "Unlimited" : String.valueOf(BORROW_LIMIT);
        String expiry = isPremium() ? " (Exp: " + vipExpiryDate + ")" : "";

        System.out.printf("%s %s (ID: %s)%s | Balance: ฿%.2f | Borrowed: %d/%s books\n",
                memberType, name, id, expiry, balance, borrowedCount, limitText);
    }
    private String escape(String data) {
        if (data == null) return "";
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            data = data.replace("\"", "\"\"");
            return "\"" + data + "\"";
        }
        return data;
    }

    public String toCSV() {
        String vipStr = (vipExpiryDate != null) ? vipExpiryDate.toString() : "null";

        return escape(id) + ","
            + escape(name) + ","
            + balance + ","
            + borrowedCount + ","
            + vipStr;
    }
}