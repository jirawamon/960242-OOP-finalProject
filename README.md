# 📚 Smart Library System

A console-based Java library management system built as a final project for the **960242 Object-Oriented Programming** course. The system supports two distinct roles — **Admin** and **User** — with CSV-backed persistence, VIP membership, wallet management, and fine calculation.

---

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Class Hierarchy & Design](#class-hierarchy--design)
- [Classes & Responsibilities](#classes--responsibilities)
  - [Interfaces](#interfaces)
  - [Abstract Classes](#abstract-classes)
  - [Concrete Classes](#concrete-classes)
  - [Menu Classes](#menu-classes)
- [Data Persistence](#data-persistence)
  - [members.csv](#memberscsv)
  - [items.csv](#itemscsv)
- [Application Flow](#application-flow)
- [OOP Concepts Used](#oop-concepts-used)
- [How to Run](#how-to-run)
- [Default Accounts](#default-accounts)
- [Admin Capabilities](#admin-capabilities)
- [User Capabilities](#user-capabilities)

---

## Features

| Feature | Description |
|---|---|
| **Dual-role access** | Separate Admin and User menus with role-appropriate actions |
| **Two item types** | `PhysicalBook` (shelf location, ฿10/day late fine) and `EBook` (URL + file size, no late fines) |
| **Borrowing system** | 7-day borrow window, wallet balance deducted on borrow |
| **VIP membership** | Monthly (฿150) or yearly (฿1,500) plans; VIP members have unlimited borrows |
| **Fine calculation** | Physical books charge ฿10 per late day; e-books are always fine-free |
| **Wallet top-up** | Users can add funds to their account at any time |
| **CSV persistence** | All member and item data is loaded at startup and saved on exit |
| **Self-registration** | New users can register with a custom 3-character alphanumeric ID |
| **Admin reports** | Unreturned items report and Top 3 most-borrowed items report |
| **Input validation** | All menu inputs are validated with friendly re-prompt loops |

---

## Project Structure

```
960242-OOP-finalProject/
│
├── README.md
├── items.csv                        # Persisted library items
├── members.csv                      # Persisted member accounts
│
└── Library/
    └── src/
        └── com/
            ├── LibraryApp.java      # Entry point (main method)
            ├── LibraryManager.java  # Data store & service layer
            ├── LibraryItem.java     # Abstract base for all items
            ├── PhysicalBook.java    # Physical book (extends LibraryItem)
            ├── EBook.java           # E-book (extends LibraryItem)
            ├── Member.java          # Library member / account
            ├── Borrowable.java      # Interface: borrowItem / returnItem
            ├── Menu.java            # Interface: run()
            ├── BaseMenu.java        # Abstract base for all menus
            ├── MainMenu.java        # Login / Register / Exit
            ├── AdminMenu.java       # Admin-only operations
            └── UserMenu.java        # User-facing operations
```

---

## Class Hierarchy & Design

```
Menu (interface)
  └── BaseMenu (abstract, implements Menu)
        ├── MainMenu
        ├── AdminMenu
        └── UserMenu

Borrowable (interface)
  └── LibraryItem (abstract, implements Borrowable)
        ├── PhysicalBook
        └── EBook

Member   (concrete)
LibraryManager (concrete)
LibraryApp     (entry point)
```

---

## Classes & Responsibilities

### Interfaces

#### `Borrowable`
Defines the borrow/return contract that every library item must fulfil.

| Method | Signature | Description |
|---|---|---|
| `borrowItem` | `boolean borrowItem(Member member)` | Attempts to borrow the item for a member |
| `returnItem` | `void returnItem()` | Marks the item as returned |

#### `Menu`
Single-method interface implemented by every menu class.

| Method | Signature | Description |
|---|---|---|
| `run` | `void run()` | Starts the interactive menu loop |

---

### Abstract Classes

#### `LibraryItem`
The core domain abstraction. Implements `Borrowable`. Extended by `PhysicalBook` and `EBook`.

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `String` (final) | Unique item identifier |
| `title` | `String` | Book title |
| `author` | `String` | Author name |
| `price` | `double` | Borrow fee (THB) |
| `isAvailable` | `boolean` | `true` if the item is on the shelf |
| `borrowedBy` | `Member` | Currently borrowing member (`null` if available) |
| `dueDate` | `LocalDate` | Return deadline (`null` if available) |
| `borrowCount` | `int` | Cumulative number of times borrowed |

**Key Methods:**

| Method | Description |
|---|---|
| `borrowItem(Member)` | Checks availability and `canBorrow()`, deducts wallet balance, sets `isAvailable = false`, sets due date to today + 7 days, increments `borrowCount` |
| `returnItem()` | Resets availability, due date, and borrower link; calls `member.recordReturn()` |
| `setBorrowedBy(Member)` | Bidirectional link: removes item from old borrower's list and adds to new borrower's list |
| `displayDetails()` | Short display: `[id] title – Status: Available / Borrowed by X (due: Y)` |
| `displayDetails(boolean)` | *(abstract)* Short or full display depending on flag |
| `calculateFine(int)` | *(abstract)* Fine in THB for a given number of late days |
| `toCSV()` | *(abstract)* Serialises the item to a CSV row string |

#### `BaseMenu`
Abstract base for all three menu classes. Implements `Menu`. Holds shared infrastructure.

**Fields:**

| Field | Type | Description |
|---|---|---|
| `manager` | `LibraryManager` | Shared data/service layer |
| `scanner` | `Scanner` | Shared console input scanner |

**Helper Methods:**

| Method | Description |
|---|---|
| `getIntInput(String prompt)` | Loops until a valid integer is entered; prints an error on invalid input |
| `getDoubleInput(String prompt)` | Loops until a valid double is entered; prints an error on invalid input |

---

### Concrete Classes

#### `PhysicalBook` extends `LibraryItem`

Represents a tangible book held on a physical shelf.

**Extra Fields:**

| Field | Type | Description |
|---|---|---|
| `location` | `String` | Shelf/rack identifier (e.g. `"A-12"`) |

**Key Behaviour:**
- `calculateFine(int lateDays)` — Returns `lateDays × ฿10.0`; returns `0.0` if not late
- Full `displayDetails` shows: emoji 📘, tag `[Physical]`, ID, title, borrow fee, availability status, due date, and total borrow count
- `toCSV()` produces an 11-column row: `Physical,id,title,author,price,available,borrowerId,location,null,dueDate,borrowCount`

#### `EBook` extends `LibraryItem`

Represents a digital book with a download URL.

**Extra Fields:**

| Field | Type | Description |
|---|---|---|
| `downloadUrl` | `String` | Resource URL |
| `fileSize` | `double` | Size in MB |

**Key Behaviour:**
- `calculateFine(int lateDays)` — Always returns `0.0` (e-books are never subject to late fines)
- Full `displayDetails` shows: emoji 📱, tag `[E-Book]`, ID, title, borrow fee, availability status, stream expiry, and total borrow count
- `toCSV()` produces an 11-column row: `EBook,id,title,author,price,available,borrowerId,downloadUrl,fileSize,dueDate,borrowCount`

#### `Member`

Represents a registered library member. Supports Regular and VIP tiers.

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `String` (final) | Member identifier |
| `name` | `String` (final) | Display name |
| `balance` | `double` | Wallet balance (THB) |
| `borrowedCount` | `int` | Number of currently borrowed items |
| `vipExpiryDate` | `LocalDate` | VIP expiry (`null` = Regular tier) |
| `borrowedItems` | `List<LibraryItem>` | Live list of borrowed items |
| `BORROW_LIMIT` | `int` (static final) | `3` — cap for Regular members |

**Key Methods:**

| Method | Description |
|---|---|
| `isPremium()` | Returns `true` if `vipExpiryDate` is non-null and has not passed |
| `canBorrow()` | VIP: always `true`. Regular: `borrowedCount < 3` |
| `applyVip(int months)` | Extends existing VIP or starts a new term from today |
| `addBalance(double)` | Tops up wallet, prints new balance |
| `deductBalance(double)` | Deducts only if sufficient funds exist; returns `false` without side effects otherwise |
| `payFine(double)` | Deducts a confirmed fine from the wallet |
| `recordBorrow()` / `recordReturn()` | Increments / decrements `borrowedCount` |
| `showBorrowedBooks()` | Prints each borrowed item's ID, title, and due date |
| `displayMember()` | Full profile: type icon, ID, VIP expiry, balance, borrow count vs. limit |
| `toCSV()` | Serialises to `id,name,balance,borrowedCount,vipExpiryDate` |

#### `LibraryManager`

Central data store and service layer. Owns `ArrayList<LibraryItem>` and `ArrayList<Member>`.

**Key Methods:**

| Method | Description |
|---|---|
| `addItem(LibraryItem)` / `addMember(Member)` | Appends to the respective list |
| `findItem(String id)` / `findMember(String id)` | Case-insensitive ID lookup; returns `null` if not found |
| `deleteItem(String id)` | Deletes only if the item is currently available (not borrowed) |
| `deleteMember(String id)` | Deletes only if the member has no active borrows |
| `updateItem(...)` | Updates title, author, and price of an existing item |
| `showAllItems()` / `showAllMembers()` | Full display of every record |
| `showReportUnreturned()` | Lists all currently checked-out items with borrower name and due date |
| `showReportMostBorrowed()` | Top 3 items ranked by cumulative `borrowCount` |
| `saveData()` | Writes both CSV files with a header row |
| `loadData()` | Reads CSV files on startup; seeds two default members and two default items if files are empty or missing; restores borrower relationships by cross-referencing stored `BorrowerID` values |

#### `LibraryApp`

Entry point of the application.

1. Sets `System.out` to UTF-8 (for correct rendering of Unicode/emoji in the console)
2. Creates a `LibraryManager` (which automatically triggers `loadData()`)
3. Opens a UTF-8 `Scanner` over `System.in` in a try-with-resources block
4. Creates and runs `MainMenu`
5. Calls `manager.saveData()` as a final backup after the menu loop exits

---

### Menu Classes

#### `MainMenu`

The top-level menu presented at startup.

| Option | Action |
|---|---|
| 1. Login | Prompts for a Member ID; `"admin"` (case-insensitive) → `AdminMenu`; valid member ID → `UserMenu` |
| 2. Register | Validates a 3-character alphanumeric ID (regex `^[a-zA-Z0-9]{3}$`), checks uniqueness, prompts for name, creates a new member with ฿0 balance |
| 3. Exit | Saves data and exits |

#### `AdminMenu`

Accessible by entering `"admin"` as the login ID — no password required.

| Option | Action |
|---|---|
| 1. View all members | Lists every registered member |
| 2. Edit Member VIP Status | Grant +1 month, +1 year, or revoke VIP for any member |
| 3. Delete member | Removes a member (blocked if they have unreturned items) |
| 4. Add new item | Creates a `PhysicalBook` or `EBook` with full detail prompts |
| 5. Delete item | Removes an item (blocked if it is currently borrowed) |
| 6. Report: Unreturned items | Shows all currently borrowed items |
| 7. Report: Top 3 Most Borrowed | Ranks items by cumulative borrow count |
| 8. Edit item | Updates title, author, and price of any item |
| 9. Logout | Returns to `MainMenu` |

#### `UserMenu`

Accessible after logging in with a valid member ID.

| Option | Action |
|---|---|
| 1. View all items | Lists every item in the library |
| 2. Borrow a book | Finds item by ID, deducts borrow fee from wallet, sets 7-day due date |
| 3. Return book(s) | Accepts space/comma-separated item IDs, prompts for late days, calculates and deducts fine |
| 4. Top up Wallet | Adds a specified amount to the member's wallet |
| 5. My Profile | Shows account details and currently borrowed books |
| 6. Upgrade / Renew VIP | 1-month plan (฿150) or 1-year plan (฿1,500); deducted from wallet |
| 7. Logout | Returns to `MainMenu` |

---

## Data Persistence

All data is stored in plain CSV files at the **working directory** of the JVM process (the project root by default).

### `members.csv`

```
ID,Name,Balance,BorrowedCount,ExpireDate
M01,Pound,500.0,0,2026-04-19
M02,Bom,50.0,0,null
```

| Column | Description |
|---|---|
| `ID` | Unique member identifier |
| `Name` | Display name |
| `Balance` | Wallet balance (THB) |
| `BorrowedCount` | Number of currently active borrows |
| `ExpireDate` | ISO date string (`YYYY-MM-DD`) or `"null"` for Regular members |

### `items.csv`

```
Type,ID,Title,Author,Price,Available,BorrowerID,Extra,Extra2,DueDate,BorrowCount
```

| Column | Physical Book | EBook |
|---|---|---|
| 0 | `"Physical"` | `"EBook"` |
| 1 | Item ID | Item ID |
| 2 | Title | Title |
| 3 | Author | Author |
| 4 | Price (THB) | Price (THB) |
| 5 | `true` / `false` | `true` / `false` |
| 6 | BorrowerID or `"none"` | BorrowerID or `"none"` |
| 7 | Shelf location | Download URL |
| 8 | `"null"` (padding) | File size (MB) |
| 9 | Due date or `"null"` | Due date or `"null"` |
| 10 | Cumulative borrow count | Cumulative borrow count |

Both item types share the same 11-column schema for uniform parsing. Physical books use `"null"` in column 8 as a placeholder.

---

## Application Flow

```
Start
  │
  └─► LibraryApp.main()
         │
         ├─ LibraryManager (loads CSV data)
         └─ MainMenu
               │
               ├─ [1] Login
               │     ├─ "admin" ──► AdminMenu ──► (manage members, items, reports)
               │     └─ <memberID> ──► UserMenu ──► (browse, borrow, return, wallet, VIP)
               │
               ├─ [2] Register ──► creates new Member (฿0 balance, Regular tier)
               │
               └─ [3] Exit ──► saveData() ──► program ends
```

---

## OOP Concepts Used

| Concept | Where Applied |
|---|---|
| **Encapsulation** | All fields in `LibraryItem` and `Member` are `private`; accessed only via getters/setters |
| **Inheritance** | `PhysicalBook` and `EBook` extend `LibraryItem`; `MainMenu`, `AdminMenu`, `UserMenu` extend `BaseMenu` |
| **Polymorphism** | `itemList` holds `LibraryItem` references; `displayDetails()`, `calculateFine()`, and `toCSV()` dispatch to the correct subclass at runtime |
| **Abstraction** | `LibraryItem` is abstract and defines the contract all items must implement; `BaseMenu` centralises shared menu infrastructure |
| **Interfaces** | `Borrowable` decouples the borrow contract from the class hierarchy; `Menu` enforces a single-method contract on all menus |
| **Composition** | `Member` owns a `List<LibraryItem>` (their current borrows); `LibraryManager` owns both the item and member collections |

---

## How to Run

### Prerequisites

- Java 11 or later (`javac` / `java` on your `PATH`)

### Compile

```bash
# From the project root
javac -d out Library/src/com/*.java
```

### Run

```bash
java -cp out com.LibraryApp
```

> **Note:** The program reads and writes `members.csv` and `items.csv` relative to the **current working directory** — run it from the project root so the files are found correctly.

### Using an IDE (IntelliJ IDEA / Eclipse)

1. Open the project root as a new project.
2. Mark `Library/src` as the **Sources Root**.
3. Run `LibraryApp.java` directly.

---

## Default Accounts

When no CSV data is found the system seeds the following defaults automatically:

| ID | Name | Balance | VIP | Borrow Limit |
|---|---|---|---|---|
| `M01` | Pound | ฿500 | Yes (1 month from first run) | Unlimited |
| `M02` | Bom | ฿50 | No | 3 books |
| `admin` | *(no password)* | N/A | Admin access | N/A |

Default library items seeded:

| ID | Type | Title | Author | Price |
|---|---|---|---|---|
| `B01` | PhysicalBook | The Great Gatsby | F. Scott Fitzgerald | ฿30 |
| `E01` | EBook | Clean Code | Robert C. Martin | ฿20 |

---

## Admin Capabilities

- View, add, update, and delete library items (Physical or EBook)
- View, delete, and manage VIP status for all members
- Generate a report of all currently unreturned items (with borrower and due date)
- Generate a Top 3 Most Borrowed items report

## User Capabilities

- Browse all available library items
- Borrow items (balance deducted immediately; due date = today + 7 days)
- Return items with optional late-day fine calculation (physical: ฿10/day, ebook: free)
- Top up wallet balance
- View personal profile and currently borrowed books
- Upgrade or renew VIP membership (฿150/month · ฿1,500/year)

---

## Authors

Developed as a final project for **960242 Object-Oriented Programming**.
By Sky, Mon, Pound and Bom. 
Digital Industry Integration, Collage of Arts, Media and Technology, Chiang Mai University