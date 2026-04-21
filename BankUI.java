package ui;

import model.Account;
import model.Transaction;
import service.BankService;

import java.util.List;
import java.util.Scanner;

public class BankUI {

    private final BankService bankService = new BankService();
    private final Scanner scanner = new Scanner(System.in);

    // ═══════════════════════════════════════════════════════════════════════════
    //  Entry point
    // ═══════════════════════════════════════════════════════════════════════════
    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("  Enter choice: ");
            switch (choice) {
                case 1 -> customerMenu();
                case 2 -> adminLogin();
                case 3 -> { running = false; bye(); }
                default -> System.out.println("  Invalid option. Try again.");
            }
        }
    }

    // ── Main menu ─────────────────────────────────────────────────────────────
    private void printMainMenu() {
        line('═', 55);
        System.out.println("  MAIN MENU");
        line('─', 55);
        System.out.println("  [1] Customer Portal");
        System.out.println("  [2] Admin Panel");
        System.out.println("  [3] Exit");
        line('═', 55);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  CUSTOMER MENU
    // ═══════════════════════════════════════════════════════════════════════════
    private void customerMenu() {
        boolean back = false;
        while (!back) {
            line('─', 55);
            System.out.println("  CUSTOMER PORTAL");
            line('─', 55);
            System.out.println("  [1] Open New Account");
            System.out.println("  [2] Deposit");
            System.out.println("  [3] Withdraw");
            System.out.println("  [4] Transfer Funds");
            System.out.println("  [5] Balance Enquiry");
            System.out.println("  [6] Mini Statement (Last 5)");
            System.out.println("  [7] Change PIN");
            System.out.println("  [8] Back");
            line('─', 55);

            int ch = readInt("  Enter choice: ");
            switch (ch) {
                case 1 -> openAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> balanceEnquiry();
                case 6 -> miniStatement();
                case 7 -> changePin();
                case 8 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    // ── Open account ──────────────────────────────────────────────────────────
    private void openAccount() {
        header("OPEN NEW ACCOUNT");
        String name = readString("  Full Name          : ");
        System.out.println("  Account Types: [1] SAVINGS  [2] CHECKING  [3] FIXED_DEPOSIT");
        int typeChoice = readInt("  Select type (1-3)  : ");
        String type = switch (typeChoice) {
            case 1 -> "SAVINGS";
            case 2 -> "CHECKING";
            case 3 -> "FIXED_DEPOSIT";
            default -> "";
        };
        if (type.isEmpty()) { System.out.println("  Invalid type."); return; }
        double deposit  = readDouble("  Initial Deposit ($): ");
        String pin      = readString("  Set 4-digit PIN    : ");
        String confirm  = readString("  Confirm PIN        : ");
        if (!pin.equals(confirm)) { System.out.println("  PINs do not match."); return; }

        Account acc = bankService.createAccount(name, type, deposit, pin);
        if (acc != null) {
            line('─', 55);
            System.out.println("  ✔  Account created successfully!");
            System.out.println("  Account Number : " + acc.getAccountNumber());
            System.out.println("  Account Type   : " + acc.getAccountType());
            System.out.printf ("  Balance        : $%.2f%n", acc.getBalance());
            System.out.println("  Please keep your account number safe.");
            line('─', 55);
        }
    }

    // ── Deposit ───────────────────────────────────────────────────────────────
    private void deposit() {
        header("DEPOSIT");
        String accNum = readString("  Account Number : ");
        double amount = readDouble("  Amount ($)     : ");
        if (bankService.deposit(accNum, amount)) {
            Account acc = bankService.getAccount(accNum);
            System.out.printf("  ✔  Deposit successful. New balance: $%.2f%n", acc.getBalance());
        }
    }

    // ── Withdraw ──────────────────────────────────────────────────────────────
    private void withdraw() {
        header("WITHDRAW");
        String accNum = readString("  Account Number : ");
        String pin    = readString("  PIN            : ");
        double amount = readDouble("  Amount ($)     : ");
        if (bankService.withdraw(accNum, pin, amount)) {
            Account acc = bankService.getAccount(accNum);
            System.out.printf("  ✔  Withdrawal successful. New balance: $%.2f%n", acc.getBalance());
        }
    }

    // ── Transfer ──────────────────────────────────────────────────────────────
    private void transfer() {
        header("FUND TRANSFER");
        String from   = readString("  From Account   : ");
        String pin    = readString("  PIN            : ");
        String to     = readString("  To Account     : ");
        double amount = readDouble("  Amount ($)     : ");
        if (bankService.transfer(from, pin, to, amount)) {
            System.out.printf("  ✔  $%.2f transferred to %s successfully.%n", amount, to);
        }
    }

    // ── Balance enquiry ───────────────────────────────────────────────────────
    private void balanceEnquiry() {
        header("BALANCE ENQUIRY");
        String accNum = readString("  Account Number : ");
        String pin    = readString("  PIN            : ");
        double bal    = bankService.getBalance(accNum, pin);
        if (bal >= 0) {
            Account acc = bankService.getAccount(accNum);
            System.out.println("  Account Holder : " + acc.getAccountHolderName());
            System.out.printf ("  Available Balance: $%.2f%n", bal);
        }
    }

    // ── Mini statement ────────────────────────────────────────────────────────
    private void miniStatement() {
        header("MINI STATEMENT");
        String accNum = readString("  Account Number : ");
        String pin    = readString("  PIN            : ");
        List<Transaction> txns = bankService.getMiniStatement(accNum, pin, 5);
        if (txns != null) {
            if (txns.isEmpty()) {
                System.out.println("  No transactions found.");
            } else {
                System.out.println();
                System.out.printf("  %-12s  %-20s  %-12s  %s%n",
                        "Txn ID", "Date & Time", "Amount", "Balance");
                line('─', 65);
                txns.forEach(t -> System.out.println(t));
            }
        }
    }

    // ── Change PIN ────────────────────────────────────────────────────────────
    private void changePin() {
        header("CHANGE PIN");
        String accNum  = readString("  Account Number : ");
        String oldPin  = readString("  Current PIN    : ");
        String newPin  = readString("  New PIN (4 digits): ");
        String confirm = readString("  Confirm new PIN: ");
        if (!newPin.equals(confirm)) { System.out.println("  PINs do not match."); return; }
        if (bankService.changePin(accNum, oldPin, newPin))
            System.out.println("  ✔  PIN changed successfully.");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  ADMIN PANEL
    // ═══════════════════════════════════════════════════════════════════════════
    private void adminLogin() {
        header("ADMIN LOGIN");
        String pw = readString("  Admin Password : ");
        if (!bankService.authenticateAdmin(pw)) {
            System.out.println("  ✘  Access denied.");
            return;
        }
        System.out.println("  ✔  Access granted. Welcome, Admin.");
        adminMenu();
    }

    private void adminMenu() {
        boolean back = false;
        while (!back) {
            line('─', 55);
            System.out.println("  ADMIN PANEL");
            line('─', 55);
            System.out.println("  [1] View All Accounts");
            System.out.println("  [2] Search Account by Name");
            System.out.println("  [3] Account Details & Full Statement");
            System.out.println("  [4] Freeze / Unfreeze Account");
            System.out.println("  [5] Delete Account");
            System.out.println("  [6] Bank Summary");
            System.out.println("  [7] Back");
            line('─', 55);

            int ch = readInt("  Enter choice: ");
            switch (ch) {
                case 1 -> viewAllAccounts();
                case 2 -> searchAccount();
                case 3 -> accountDetails();
                case 4 -> freezeUnfreeze();
                case 5 -> deleteAccount();
                case 6 -> bankSummary();
                case 7 -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    // ── View all ──────────────────────────────────────────────────────────────
    private void viewAllAccounts() {
        header("ALL ACCOUNTS");
        List<Account> accounts = bankService.getAllAccounts();
        if (accounts.isEmpty()) { System.out.println("  No accounts found."); return; }
        System.out.printf("  %-12s  %-20s  %-15s  %-10s  %s%n",
                "Acc No.", "Holder Name", "Type", "Balance", "Status");
        line('─', 75);
        for (Account a : accounts) {
            System.out.printf("  %-12s  %-20s  %-15s  $%-9.2f  %s%n",
                    a.getAccountNumber(), a.getAccountHolderName(),
                    a.getAccountType(), a.getBalance(),
                    a.isActive() ? "Active" : "Frozen");
        }
        System.out.printf("%n  Total accounts: %d%n", accounts.size());
    }

    // ── Search ────────────────────────────────────────────────────────────────
    private void searchAccount() {
        header("SEARCH ACCOUNT");
        String name = readString("  Enter name to search: ");
        List<Account> results = bankService.searchByName(name);
        if (results.isEmpty()) { System.out.println("  No matches found."); return; }
        results.forEach(a -> System.out.println("  " + a));
    }

    // ── Account details ───────────────────────────────────────────────────────
    private void accountDetails() {
        header("ACCOUNT DETAILS");
        String accNum = readString("  Account Number : ");
        Account acc = bankService.getAccount(accNum);
        if (acc == null) { System.out.println("  Account not found."); return; }

        line('─', 55);
        System.out.println("  Account Number  : " + acc.getAccountNumber());
        System.out.println("  Holder Name     : " + acc.getAccountHolderName());
        System.out.println("  Account Type    : " + acc.getAccountType());
        System.out.printf ("  Balance         : $%.2f%n", acc.getBalance());
        System.out.println("  Status          : " + (acc.isActive() ? "Active" : "Frozen"));
        System.out.println("  Created At      : " + acc.getFormattedCreatedAt());
        line('─', 55);
        System.out.println("  TRANSACTION HISTORY");
        line('─', 65);
        System.out.printf("  %-12s  %-20s  %-14s  %-14s  %s%n",
                "Txn ID", "Date & Time", "Type", "Amount", "Balance");
        line('─', 75);

        List<Transaction> txns = acc.getTransactions();
        if (txns.isEmpty()) {
            System.out.println("  No transactions.");
        } else {
            txns.forEach(t -> System.out.printf(
                "  %-12s  %-20s  %-14s  $%-13.2f  $%.2f%n",
                t.getTransactionId(), t.getFormattedTime(),
                t.getType(), t.getAmount(), t.getBalanceAfter()));
        }
        System.out.printf("%n  Total transactions: %d%n", txns.size());
    }

    // ── Freeze / Unfreeze ─────────────────────────────────────────────────────
    private void freezeUnfreeze() {
        header("FREEZE / UNFREEZE ACCOUNT");
        String accNum = readString("  Account Number : ");
        Account acc = bankService.getAccount(accNum);
        if (acc == null) { System.out.println("  Account not found."); return; }

        System.out.println("  Current status : " + (acc.isActive() ? "Active" : "Frozen"));
        System.out.println("  [1] Freeze  [2] Unfreeze");
        int ch = readInt("  Choice: ");
        boolean newStatus = (ch == 2);
        if (bankService.setAccountStatus(accNum, newStatus))
            System.out.println("  ✔  Account " + (newStatus ? "unfrozen" : "frozen") + " successfully.");
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    private void deleteAccount() {
        header("DELETE ACCOUNT");
        String accNum  = readString("  Account Number     : ");
        String confirm = readString("  Type 'DELETE' to confirm: ");
        if (!"DELETE".equals(confirm)) { System.out.println("  Cancelled."); return; }
        if (bankService.deleteAccount(accNum))
            System.out.println("  ✔  Account deleted.");
    }

    // ── Bank summary ──────────────────────────────────────────────────────────
    private void bankSummary() {
        header("BANK SUMMARY");
        List<Account> all = bankService.getAllAccounts();
        long savings = all.stream().filter(a -> a.getAccountType().equals("SAVINGS")).count();
        long checking = all.stream().filter(a -> a.getAccountType().equals("CHECKING")).count();
        long fixed = all.stream().filter(a -> a.getAccountType().equals("FIXED_DEPOSIT")).count();
        long active = all.stream().filter(Account::isActive).count();
        long frozen = all.size() - active;

        System.out.printf("  Total Accounts     : %d%n", all.size());
        System.out.printf("    Savings          : %d%n", savings);
        System.out.printf("    Checking         : %d%n", checking);
        System.out.printf("    Fixed Deposit    : %d%n", fixed);
        System.out.printf("  Active             : %d%n", active);
        System.out.printf("  Frozen             : %d%n", frozen);
        System.out.printf("  Total Deposits     : $%.2f%n", bankService.getTotalDeposits());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════════════════════════════════════
    private void printBanner() {
        System.out.println();
        line('═', 55);
        System.out.println("        JAVA BANK MANAGEMENT SYSTEM");
        System.out.println("             Secure | Reliable | Fast");
        line('═', 55);
        System.out.println();
    }

    private void header(String title) {
        System.out.println();
        line('─', 55);
        System.out.println("  " + title);
        line('─', 55);
    }

    private void line(char ch, int len) {
        System.out.println("  " + String.valueOf(ch).repeat(len));
    }

    private void bye() {
        System.out.println();
        line('═', 55);
        System.out.println("  Thank you for using Java Bank. Goodbye!");
        line('═', 55);
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        try { return Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid amount."); return -1; }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
