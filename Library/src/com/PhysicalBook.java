package com;

public class PhysicalBook extends LibraryItem {
    private String location;

    public PhysicalBook(String id, String title, String author, double price, String location) {
        super(id, title, author, price);
        this.location = location;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public double calculateFine(int lateDays) {
        return lateDays > 0 ? lateDays * 10.0 : 0.0;
    }

    @Override
    public void displayDetails(boolean showFull) {
        if (showFull) {
            String dueStr = isAvailable() ? "-" : getDueDate().toString();
            System.out.printf("📘 [Physical] ID: %s | Title: %s | Borrow fee: ฿%.2f | Status: %s | Due date: %s | 📈 Total borrows: %d times\n",
                    getId(), getTitle(), getPrice(), isAvailable() ? "Available" : "Checked Out", dueStr, getBorrowCount());
        } else {
            super.displayDetails();
        }
    }

    protected String escape(String data) {
        if (data == null) return "";
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            data = data.replace("\"", "\"\"");
            return "\"" + data + "\"";
        }
        return data;
    }

    @Override
    public String toCSV() {
        String borrowerId = (getBorrowedBy() != null) ? getBorrowedBy().getId() : "none";
        String dueStr = (getDueDate() != null) ? getDueDate().toString() : "null";

        return "Physical," 
            + escape(getId()) + ","
            + escape(getTitle()) + ","
            + escape(getAuthor()) + ","
            + getPrice() + ","
            + isAvailable() + ","
            + borrowerId + ","
            + escape(location) + ","
            + "null,"   // ให้ column ตรงกับ EBook
            + dueStr + ","
            + getBorrowCount();
    }
}