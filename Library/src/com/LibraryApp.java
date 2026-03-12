package com;

import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        LibraryManager manager = new LibraryManager();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=========================================");
        System.out.println(" 🌟 ระบบห้องสมุดอัจฉริยะ (VIP & Wallet) 🌟");
        System.out.println("=========================================");

        while (running) {
            System.out.println("\n--- เมนูหลัก ---");
            System.out.println("1. ดูรายการหนังสือทั้งหมด");
            System.out.println("2. ดูรายชื่อสมาชิก");
            System.out.println("3. สมัครสมาชิกใหม่ (เลือก VIP ได้)");
            System.out.println("4. เติมเงินเข้า Wallet");
            System.out.println("5. ยืมหนังสือ (หักเงินทันที & กำหนดวันคืน)");
            System.out.println("6. คืนหนังสือ (ระบบคำนวณค่าปรับล่าช้า)");
            System.out.println("7. แก้ไขข้อมูลหนังสือ");
            System.out.println("8. ออกจากระบบ (บันทึกไฟล์)");
            System.out.print("👉 เลือกเมนู: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: manager.showAllItems(); break;
                    case 2: manager.showAllMembers(); break;
                    case 3:
                        System.out.print("ตั้งรหัสสมาชิก (เช่น M03): ");
                        String mId = scanner.nextLine();
                        if (manager.findMember(mId) != null) {
                            System.out.println("❌ ไม่สามารถสมัครได้: รหัสซ้ำ!");
                            break;
                        }
                        System.out.print("ชื่อสมาชิก: ");
                        String mName = scanner.nextLine();
                        System.out.print("เติมเงินแรกเข้า (บาท): ");
                        double initialMoney = scanner.nextDouble();

                        System.out.println("เลือกประเภทสมาชิก:");
                        System.out.println("1. สมาชิกทั่วไป (จำกัดการยืม 3 เล่ม)");
                        System.out.println("2. สมาชิก VIP (ยืมไม่อั้น!)");
                        System.out.print("เลือก (1 หรือ 2): ");
                        int type = scanner.nextInt();
                        boolean isVip = (type == 2);

                        manager.addMember(new Member(mId, mName, initialMoney, 0, isVip));
                        System.out.println("✅ สมัครสมาชิกสำเร็จ!");
                        break;
                    case 4:
                        System.out.print("กรอกรหัสสมาชิก: ");
                        Member mTopup = manager.findMember(scanner.nextLine());
                        if (mTopup != null) {
                            System.out.print("จำนวนเงินที่ต้องการเติม: ");
                            mTopup.addBalance(scanner.nextDouble());
                        } else { System.out.println("❌ ไม่พบรหัสนี้"); }
                        break;
                    case 5:
                        System.out.print("รหัสหนังสือที่ต้องการยืม: ");
                        LibraryItem itemToBorrow = manager.findItem(scanner.nextLine());
                        if (itemToBorrow != null) {
                            System.out.print("รหัสสมาชิกผู้ยืม: ");
                            Member mBorrow = manager.findMember(scanner.nextLine());
                            if (mBorrow != null) {
                                if(itemToBorrow.borrowItem(mBorrow)) {
                                    System.out.println("✅ ยืมสำเร็จ! หักเงิน " + itemToBorrow.getPrice() + " บาท");
                                    System.out.println("📅 กำหนดคืนวันที่: " + itemToBorrow.getDueDate());
                                }
                            } else { System.out.println("❌ ไม่พบรหัสสมาชิก"); }
                        } else { System.out.println("❌ ไม่พบรหัสหนังสือ"); }
                        break;
                    case 6:
                        System.out.print("รหัสหนังสือที่ต้องการคืน: ");
                        LibraryItem itemToReturn = manager.findItem(scanner.nextLine());
                        if (itemToReturn != null && !itemToReturn.isAvailable()) {
                            Member borrower = itemToReturn.getBorrowedBy();

                            // จำลองเวลาในระบบโดยให้กรอกว่าคืนช้ากี่วัน
                            System.out.print("คืนช้ากว่ากำหนดกี่วัน? (ถ้าตรงเวลาพิมพ์ 0): ");
                            int lateDays = scanner.nextInt();

                            double fine = itemToReturn.calculateFine(lateDays);
                            if (fine > 0) {
                                borrower.payFine(fine); // หักค่าปรับจาก Wallet สมาชิก
                            }

                            itemToReturn.returnItem();
                            System.out.println("✅ ทำรายการคืนหนังสือสำเร็จ!");

                        } else { System.out.println("❌ หนังสือว่างอยู่ หรือรหัสผิด"); }
                        break;
                    case 7:
                        System.out.print("รหัสหนังสือที่ต้องการแก้ไขราคา: ");
                        LibraryItem itemToEdit = manager.findItem(scanner.nextLine());
                        if (itemToEdit != null) {
                            System.out.print("กรอกราคาใหม่: ");
                            itemToEdit.setPrice(scanner.nextDouble());
                            System.out.println("✅ อัปเดตราคาใหม่เรียบร้อยผ่าน Setter!");
                        } else { System.out.println("❌ ไม่พบรหัสหนังสือ"); }
                        break;
                    case 8:
                        manager.saveData();
                        System.out.println("💾 บันทึกข้อมูลเรียบร้อย ขอบคุณครับ!");
                        running = false;
                        break;
                    default: System.out.println("⚠️ เลือก 1-8 เท่านั้น");
                }
            } catch (Exception e) {
                System.out.println("❌ กรุณาพิมพ์ให้ถูกต้อง!");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}