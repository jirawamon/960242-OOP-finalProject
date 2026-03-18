package com;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LibraryManager {
    private final ArrayList<LibraryItem> itemList = new ArrayList<>();
    private final ArrayList<Member> memberList = new ArrayList<>();

    public LibraryManager() {
        loadData();
    }

    public void addMember(Member m) { memberList.add(m); }
    public void addItem(LibraryItem i) { itemList.add(i); }

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

    public boolean deleteMember(String id) {
        Member m = findMember(id);
        if (m != null) {
            if (m.getBorrowedCount() == 0) {
                memberList.remove(m);
                return true;
            } else {
                System.out.println("❌ Cannot delete! This member has unreturned books.");
                return false;
            }
        }
        return false;
    }

    public boolean deleteItem(String id) {
        LibraryItem item = findItem(id);
        if (item != null) {
            if (item.isAvailable()) {
                itemList.remove(item);
                return true;
            } else {
                System.out.println("❌ Cannot delete! This item is currently checked out.");
                return false;
            }
        }
        return false;
    }

    public boolean updateItem(String id, String newTitle, String newAuthor, double newPrice) {
    LibraryItem item = findItem(id);

        if (item != null) {
            item.setTitle(newTitle);
            item.setAuthor(newAuthor);
            item.setPrice(newPrice);

            return true;
        }
        return false;
    }

    public void showAllItems() {
        System.out.println("\n--- 📚 All Library Items ---");
        for (LibraryItem item : itemList) item.displayDetails(true);
    }

    public void showAllMembers() {
        System.out.println("\n--- 👥 All Members ---");
        for (Member m : memberList) m.displayMember();
    }

    public void showReportUnreturned() {
        System.out.println("\n--- 📊 Report: Unreturned / Checked-Out Items ---");
        boolean found = false;
        for (LibraryItem item : itemList) {
            if (!item.isAvailable()) {
                System.out.printf("- [%s] %s | Borrowed by: %s | Due: %s\n",
                        item.getId(), item.getTitle(), item.getBorrowedBy().getName(), item.getDueDate());
                found = true;
            }
        }
        if (!found) System.out.println("✅ No items currently checked out.");
    }

    public void showReportMostBorrowed() {
        System.out.println("\n--- 🏆 Report: Most Borrowed Items (Top 3) ---");
        ArrayList<LibraryItem> sortedList = new ArrayList<>(itemList);
        sortedList.sort((a, b) -> Integer.compare(b.getBorrowCount(), a.getBorrowCount()));

        int rank = 1;
        for (LibraryItem item : sortedList) {
            if (item.getBorrowCount() > 0) {
                System.out.printf("%d. [%s] %s - Borrowed %d times\n", rank++, item.getId(), item.getTitle(), item.getBorrowCount());
                if (rank > 3) break;
            }
        }
        if (rank == 1) System.out.println("📉 No borrowing history yet.");
    }
        private String[] parseCSV(String line) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    public void saveData() {
        try (PrintWriter w1 = new PrintWriter(new FileWriter("members.csv"));
            PrintWriter w2 = new PrintWriter(new FileWriter("items.csv"))) {

            // HEADER
            w1.println("ID,Name,Balance,BorrowedCount,ExpireDate");
            w2.println("Type,ID,Title,Author,Price,Available,BorrowerID,Extra,Extra2,DueDate,BorrowCount");

            // DATA
            for (Member m : memberList) {
                w1.println(m.toCSV());
            }

            for (LibraryItem i : itemList) {
                w2.println(i.toCSV());
            }

            

        } catch (Exception e) {
            System.out.println("❌ Error saving data.");
        }
    }

    private void loadData() {
        File memberFile = new File("members.csv");
        File itemFile = new File("items.csv");

        if (!memberFile.exists() || !itemFile.exists()) {
            memberList.add(new Member("M01", "Pound", 500.0, 0, LocalDate.now().plusMonths(1)));
            memberList.add(new Member("M02", "Bom", 50.0, 0, null));
            itemList.add(new PhysicalBook("B01", "Java 101", "Dr. Thama", 20.0, "A-12"));
            itemList.add(new EBook("E01", "OOP Guide", "Hydra", 15.0, "dii.com/oop", 5.5));
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(memberFile))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    LocalDate exp = null;
                    // 📌 ดักจับเผื่อไฟล์เก่าของคุณยังเก็บเป็น "true"/"false"
                    if (!parts[4].equals("null") && !parts[4].equals("false")) {
                        if (parts[4].equals("true")) {
                            exp = LocalDate.now().plusMonths(1); // แก้บัคให้คนเก่าได้เป็น VIP 1 เดือน
                        } else {
                            exp = LocalDate.parse(parts[4]); // โหลดวันที่จากไฟล์
                        }
                    }
                    memberList.add(new Member(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), exp));
                }
            }
        } catch (Exception e) { }

        try (BufferedReader br = new BufferedReader(new FileReader(itemFile))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    LibraryItem item;
                    if (parts[0].equals("Physical")) {
                        item = new PhysicalBook(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]), parts[7]);
                        if (!parts[8].equals("null")) item.setDueDate(LocalDate.parse(parts[8]));
                        if (parts.length >= 10) item.setBorrowCount(Integer.parseInt(parts[9]));
                    } else {
                        item = new EBook(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]), parts[7], Double.parseDouble(parts[8]));
                        if (parts.length >= 10 && !parts[9].equals("null")) item.setDueDate(LocalDate.parse(parts[9]));
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
        } catch (Exception e) { }
    }

}