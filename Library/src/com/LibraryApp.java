package com;

import java.util.InputMismatchException;
import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        LibraryManager manager = new LibraryManager();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("==================================");
        System.out.println(" 📚 ยินดีต้อนรับสู่ระบบห้องสมุด 📚");
        System.out.println("==================================");

        while (running) {
            System.out.println("\n--- เมนูหลัก ---");
            System.out.println("1. ดูรายการสื่อทั้งหมด");
            System.out.println("2. เพิ่มสื่อใหม่เข้าระบบ (จัดการข้อมูล)");
            System.out.println("3. ยืมสื่อ");
            System.out.println("4. คืนสื่อ (และคิดค่าปรับ)");
            System.out.println("5. ดูรายงานสรุป");
            System.out.println("6. ออกจากระบบ (บันทึกข้อมูล)");
            System.out.print("👉 เลือกเมนู (1-6): ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // เคลียร์ Enter

                switch (choice) {
                    case 1:
                        manager.displayAllMedia();
                        break;
                    case 2:
                        System.out.println("\n--- เพิ่มสื่อใหม่ ---");
                        System.out.println("1. หนังสือเล่ม (Book)");
                        System.out.println("2. สื่อดิจิทัล (Digital Media)");
                        System.out.print("เลือกประเภทสื่อ (1-2): ");
                        int type = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("ตั้งรหัสสื่อ (เช่น B03, D02): ");
                        String id = scanner.nextLine();

                        // 🛑 จุดที่แก้บั๊ก: เช็ครหัสซ้ำตรงนี้ 🛑
                        if (manager.findMedia(id) != null) {
                            System.out.println("❌ ไม่สามารถเพิ่มได้: รหัส '" + id + "' มีซ้ำอยู่ในระบบแล้ว!");
                            break; // เตะกลับไปหน้าเมนูหลักทันที
                        }

                        System.out.print("ชื่อเรื่อง: ");
                        String title = scanner.nextLine();

                        if (type == 1) {
                            System.out.print("ชื่อผู้แต่ง: ");
                            String author = scanner.nextLine();
                            manager.addMedia(new Book(id, title, author));
                        } else if (type == 2) {
                            System.out.print("ประเภทไฟล์ (เช่น PDF, MP4): ");
                            String format = scanner.nextLine();
                            manager.addMedia(new DigitalMedia(id, title, format));
                        } else {
                            System.out.println("❌ เลือกประเภทไม่ถูกต้อง");
                        }
                        break;
                    case 3:
                        System.out.print("\nกรอกรหัสสื่อที่ต้องการยืม (เช่น B01, D01): ");
                        String borrowId = scanner.nextLine();
                        Media itemToBorrow = manager.findMedia(borrowId);

                        if (itemToBorrow != null) {
                            if (itemToBorrow.isAvailable()) {
                                System.out.print("กรอกชื่อผู้ยืม: ");
                                String memberName = scanner.nextLine();
                                itemToBorrow.borrowItem(memberName);
                                System.out.println("✅ ทำรายการยืมสำเร็จโดยคุณ " + memberName);
                            } else {
                                System.out.println("❌ ขออภัย สื่อนี้ถูกยืมไปแล้วโดยคุณ " + itemToBorrow.getBorrowerName());
                            }
                        } else {
                            System.out.println("❌ ไม่พบรหัสสื่อนี้ในระบบ");
                        }
                        break;
                    case 4:
                        System.out.print("\nกรอกรหัสสื่อที่ต้องการคืน: ");
                        String returnId = scanner.nextLine();
                        Media itemToReturn = manager.findMedia(returnId);

                        if (itemToReturn != null && !itemToReturn.isAvailable()) {
                            itemToReturn.returnItem();
                            System.out.print("คืนช้ากว่ากำหนดกี่วัน? (ถ้าไม่ช้าให้พิมพ์ 0): ");
                            int lateDays = scanner.nextInt();
                            double fine = itemToReturn.calculateFine(lateDays);

                            System.out.println("✅ ทำรายการคืนสำเร็จ!");
                            if (fine > 0) {
                                System.out.println("💸 มีค่าปรับชำระ: " + fine + " บาท");
                            } else {
                                System.out.println("💸 ไม่มีค่าปรับเพิ่มเติม");
                            }
                        } else {
                            System.out.println("❌ ไม่พบสื่อนี้ หรือสื่อยังไม่ได้ถูกยืมออกไป");
                        }
                        break;
                    case 5:
                        manager.showReports();
                        break;
                    case 6:
                        manager.saveToCSV();
                        System.out.println("\n💾 บันทึกข้อมูลลงไฟล์ library_data.csv เรียบร้อยแล้ว");
                        System.out.println("👋 ขอบคุณที่ใช้บริการครับ!");
                        running = false;
                        break;
                    default:
                        System.out.println("⚠️ กรุณาเลือกเมนู 1-6 เท่านั้น");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ ข้อผิดพลาด: กรุณาพิมพ์เฉพาะตัวเลขเท่านั้น!");
                scanner.nextLine(); // เคลียร์ขยะที่พิมพ์ผิดทิ้ง
            } catch (Exception e) {
                System.out.println("❌ เกิดข้อผิดพลาดระบบ: " + e.getMessage());
            }
        }
        scanner.close();
    }
}