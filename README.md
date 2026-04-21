# Java Bank Management System

A complete, object-oriented Bank Management System built in Java.

---

## Features

### Customer Portal
| Feature | Description |
|--------|-------------|
| Open Account | Create SAVINGS, CHECKING, or FIXED_DEPOSIT accounts |
| Deposit | Add funds to any account |
| Withdraw | Withdraw with PIN authentication |
| Transfer | Transfer funds between accounts |
| Balance Enquiry | Check balance with PIN |
| Mini Statement | View last 5 transactions |
| Change PIN | Update 4-digit PIN securely |

### Admin Panel (Password: `admin123`)
| Feature | Description |
|--------|-------------|
| View All Accounts | Paginated list of all accounts |
| Search by Name | Find accounts by holder name |
| Account Details | Full transaction history |
| Freeze / Unfreeze | Control account access |
| Delete Account | Permanently remove account |
| Bank Summary | Total deposits, account type breakdown |

---

## Project Structure

```
BankManagementSystem/
├── src/
│   ├── Main.java                  ← Entry point
│   ├── model/
│   │   ├── Account.java           ← Account entity
│   │   └── Transaction.java       ← Transaction entity
│   ├── service/
│   │   └── BankService.java       ← Business logic
│   └── ui/
│       └── BankUI.java            ← Console UI
└── README.md
```

---

## How to Compile & Run

### Prerequisites
- Java JDK 11 or higher

### Step 1 — Compile
```bash
cd src
mkdir -p ../out
javac -d ../out model/Transaction.java model/Account.java service/BankService.java ui/BankUI.java Main.java
```

### Step 2 — Run
```bash
java -cp ../out Main
```

---

## Demo Accounts (pre-loaded)

| Account Number | Holder       | Type          | Balance    | PIN  |
|----------------|--------------|---------------|------------|------|
| ACC001001      | Alice Johnson | SAVINGS       | $6,300.00  | 1234 |
| ACC001002      | Bob Smith     | CHECKING      | $2,300.00  | 5678 |
| ACC001003      | Carol White   | FIXED_DEPOSIT | $10,000.00 | 9999 |

---

## Business Rules

- **SAVINGS**: Minimum balance $500; minimum initial deposit $500
- **CHECKING**: No minimum balance; any initial deposit
- **FIXED_DEPOSIT**: Minimum initial deposit $1,000
- **PIN**: Must be exactly 4 numeric digits
- **Transfers**: Require source account PIN; source must have sufficient funds

---

## OOP Design

| Class | Responsibility |
|-------|---------------|
| `Account` | Encapsulates account data & operations (deposit, withdraw, PIN) |
| `Transaction` | Immutable record of each financial operation |
| `BankService` | Central business logic and account registry |
| `BankUI` | Console I/O and menu navigation |

---

## Sample Session

```
  ═══════════════════════════════════════════════════════
        JAVA BANK MANAGEMENT SYSTEM
             Secure | Reliable | Fast
  ═══════════════════════════════════════════════════════

  MAIN MENU
  ─────────────────────────────────────────────────────
  [1] Customer Portal
  [2] Admin Panel
  [3] Exit
```
