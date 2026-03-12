package com;

public class PhysicalBook extends LibraryItem {
    private String location;

    public PhysicalBook(String id, String title, String author, double price, String location) {
        super(id, title, author, price);
        this.location = location;
    }

    public void setLocation(String location) { this.location = location; }

    @Override
    public double calculateFine(int lateDays) {
        return lateDays > 0 ? lateDays * 10.0 : 0.0;
    }

    @Override
    public void displayDetails(boolean showFull) {
        if (showFull) {
            String dueStr = isAvailable ? "-" : dueDate.toString();
            // 📌 เพิ่มการปริ้นท์ borrowCount
            System.out.printf("📘 [Physical] รหัส: %s | เรื่อง: %s | ราคายืม: %.2f บ. | สถานะ: %s | กำหนดคืน: %s | 📈 ยอดถูกยืมสะสม: %d ครั้ง\n",
                    id, title, price, isAvailable ? "ว่าง" : "ยืมแล้ว", dueStr, borrowCount);
        } else {
            super.displayDetails();
        }
    }

    @Override
    public String toCSV() {
        String borrowerId = (borrowedBy != null) ? borrowedBy.getId() : "none";
        String dueStr = (dueDate != null) ? dueDate.toString() : "null";
        // 📌 เซฟ borrowCount ลงไฟล์ด้วย
        return "Physical," + id + "," + title + "," + author + "," + price + "," + isAvailable + "," + borrowerId + "," + location + "," + dueStr + "," + borrowCount;
    }
}