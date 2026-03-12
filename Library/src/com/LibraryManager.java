package com;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LibraryManager {
    private ArrayList<LibraryItem> itemList = new ArrayList<>();
    private ArrayList<Member> memberList = new ArrayList<>();

    public LibraryManager() {
        loadData();
    }

    public void addMember(Member m) {
        memberList.add(m);
    }

    public void addItem(LibraryItem i) {
        itemList.add(i);
    }

    public Member findMember(String id) {
        for (Member m : memberList) {
            if (m.getId().equalsIgnoreCase(id)) return m;
        }
        return null;
    }

    public LibraryItem findItem(String id) {
        for (LibraryItem item : itemList) {
            if (item.getId().equalsIgnoreCase(id)) return item;
        }
        return null;
    }

    public void showAllItems() {
        System.out.println("\n--- 📚 รายการหนังสือทั้งหมด ---");
        for (LibraryItem item : itemList) {
            item.displayDetails(true);
        }
    }

    public void showAllMembers() {
        System.out.println("\n--- 👥 รายชื่อสมาชิกทั้งหมด ---");
        for (Member m : memberList) {
            m.displayMember();
        }
    }

    public void saveData() {
        try (PrintWriter w1 = new PrintWriter(new FileWriter("members.csv"));
             PrintWriter w2 = new PrintWriter(new FileWriter("items.csv"))) {
            for (Member m : memberList) w1.println(m.toCSV());
            for (LibraryItem i : itemList) w2.println(i.toCSV());
        } catch (Exception e) {
            System.out.println("❌ Error saving data.");
        }
    }

    private void loadData() {
        File memberFile = new File("members.csv");
        File itemFile = new File("items.csv");

        if (!memberFile.exists() || !itemFile.exists()) {
            memberList.add(new Member("M01", "Pound", 500.0, 0, true));
            memberList.add(new Member("M02", "Alice", 50.0, 0, false));
            itemList.add(new PhysicalBook("B01", "Java 101", "Dr. Thama", 20.0, "A-12"));
            itemList.add(new EBook("E01", "OOP Guide", "Hydra", 15.0, "dii.com/oop", 5.5));
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(memberFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    memberList.add(new Member(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), Boolean.parseBoolean(parts[4])));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ โหลดไฟล์สมาชิกล้มเหลว");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(itemFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    LibraryItem item;
                    if (parts[0].equals("Physical")) {
                        item = new PhysicalBook(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]), parts[7]);
                        if (!parts[8].equals("null")) item.setDueDate(LocalDate.parse(parts[8]));
                        // 📌 ดึงยอดประวัติจากไฟล์ (คอลัมน์ที่ 10)
                        if (parts.length >= 10) item.setBorrowCount(Integer.parseInt(parts[9]));
                    } else {
                        item = new EBook(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]), parts[7], Double.parseDouble(parts[8]));
                        if (parts.length >= 10 && !parts[9].equals("null")) item.setDueDate(LocalDate.parse(parts[9]));
                        // 📌 ดึงยอดประวัติจากไฟล์ (คอลัมน์ที่ 11)
                        if (parts.length >= 11) item.setBorrowCount(Integer.parseInt(parts[10]));
                    }

                    if (parts[5].equals("false")) {
                        item.setAvailable(false);
                        Member borrower = findMember(parts[6]);
                        if (borrower != null) item.setBorrowedBy(borrower);
                    }
                    itemList.add(item);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ โหลดไฟล์หนังสือล้มเหลว");
        }
    }
}