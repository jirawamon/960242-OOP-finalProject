package com;

public class EBook extends LibraryItem {
    private String downloadUrl;
    private double fileSize;

    public EBook(String id, String title, String author, double price, String downloadUrl, double fileSize) {
        super(id, title, author, price);
        this.downloadUrl = downloadUrl;
        this.fileSize = fileSize;
    }

    public String getDownloadUrl() { return downloadUrl; }
    public double getFileSize() { return fileSize; }
    public void setDownloadUrl(String url) { this.downloadUrl = url; }
    public void setFileSize(double size) { this.fileSize = size; }

    @Override
    public double calculateFine(int lateDays) {
        return 0.0;
    }

    @Override
    public void displayDetails(boolean showFull) {
        if (showFull) {
            String dueStr = isAvailable() ? "-" : getDueDate().toString();
            System.out.printf("📱 [E-Book] ID: %s | Title: %s | Borrow fee: ฿%.2f | Status: %s | Stream expires: %s | 📈 Total borrows: %d times\n",
                    getId(), getTitle(), getPrice(), isAvailable() ? "Available" : "Checked Out", dueStr, getBorrowCount());
        } else {
            super.displayDetails();
        }
    }

    @Override
    public String toCSV() {
        String borrowerId = (getBorrowedBy() != null) ? getBorrowedBy().getId() : "none";
        String dueStr = (getDueDate() != null) ? getDueDate().toString() : "null";
        return "EBook," + getId() + "," + getTitle() + "," + getAuthor() + "," + getPrice() + "," + isAvailable() + "," + borrowerId + "," + downloadUrl + "," + fileSize + "," + dueStr + "," + getBorrowCount();
    }
}