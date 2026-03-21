# 960242-OOP-finalProject


### Smart Library System (Auto-VIP & Wallet Integration)

A robust Command-Line Interface (CLI) based Library Management System developed in Java. This project focuses on high-level Object-Oriented Programming (OOP) principles, featuring a dynamic membership system with automated VIP logic and an integrated electronic wallet for seamless transactions.

## 📝 Project Overview

The Smart Library System is designed to streamline library operations including inventory management, member tracking, and financial transactions. It distinguishes between physical assets and digital media, applying specific business logic and fine calculations to each category.

## ✨ Key Features

### 👤 Advanced Membership System
- **Tiered Access:** Supports both Regular and VIP membership tiers.
- **Automated VIP Logic:** Integrated subscription management for 1-month and 1-year plans with automated expiry tracking.
- **Dynamic Limits:** Regular members are subject to a 3-book limit, while VIP members enjoy unlimited borrowing privileges.

### 💳 Electronic Wallet Integration
- **Prepaid Balance:** Users can top up their accounts to maintain a balance for borrowing fees and VIP upgrades.
- **Automated Fine Payment:** System calculates and deducts late fines directly from the user's wallet upon item return.
- **Transactional Security:** Ensures sufficient funds before allowing borrows or status upgrades.

### 📚 Specialized Inventory Management
- **Physical Books:** Includes location tracking and daily fine accumulation (10 THB/day).
- **E-Books:** Features digital access via download URLs and size tracking with no late fees.
- **Real-time Availability:** Tracks borrowing status, borrower ID, and due dates dynamically.

### 🛡️ Administrative & Analytical Tools
- **System Oversight:** Admin-level access to manage inventory (CRUD) and modify member VIP status.
- **Reporting Engine:** Generates real-time reports for unreturned items and identifies the "Top 3 Most Borrowed" assets.

## 🏗️ Technical Architecture (OOP Principles)

This application serves as a demonstration of scalable software design patterns:

- **Inheritance:** Specialized logic for `PhysicalBook` and `EBook` extending the `LibraryItem` base class.
- **Polymorphism:** Method overriding for `calculateFine()` and `displayDetails()` to provide type-specific behavior.
- **Abstraction:** Use of interfaces like `Menu` and `Borrowable` to decouple implementation from behavior.
- **Encapsulation:** Strict data protection using private fields and controlled access through getters and setters.
- **Data Persistence:** Custom CSV engine for loading and saving system state to `members.csv` and `items.csv`.

## 🛠️ Technology Stack
- **Language:** Java 8+
- **Input/Output:** Standard I/O with UTF-8 support
- **Time Management:** Java Time API (`LocalDate`)
- **Storage:** Flat-file CSV System

## 📦 Installation & Usage

1. **Prerequisites:** Ensure you have JDK 8 or higher installed on your system.
2. **Compilation:**
   ```bash
   javac com/*.java

#### Structure

### Menu System
- `Menu` (interface)
  - `BaseMenu` (implements Menu)
    - `AdminMenu`
    - `MainMenu`
    - `UserMenu`

### Library Item System
- `Borrowable` (interface)
  - `LibraryItem` (implements Borrowable)
    - `EBook`
    - `PhysicalBook`

### Core Classes
- `LibraryApp`
- `LibraryManager`
- `Member`
