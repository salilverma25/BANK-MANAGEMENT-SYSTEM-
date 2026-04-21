package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountNumber;
    private String accountHolderName;
    private String accountType; // SAVINGS, CHECKING, FIXED_DEPOSIT
    private double balance;
    private String pin;
    private boolean isActive;
    private LocalDateTime createdAt;
    private List<Transaction> transactions;

    private static final double SAVINGS_MIN_BALANCE = 500.0;
    private static final double CHECKING_MIN_BALANCE = 0.0;

    public Account(String accountNumber, String accountHolderName, String accountType,
                   double initialDeposit, String pin) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = initialDeposit;
        this.pin = pin;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();

        // Record initial deposit
        transactions.add(new Transaction("DEPOSIT", initialDeposit, balance, "Initial deposit"));
    }

    // ── Deposit ──────────────────────────────────────────────────────────────
    public boolean deposit(double amount) {
        if (!isActive) { System.out.println("  Account is inactive."); return false; }
        if (amount <= 0)  { System.out.println("  Amount must be positive."); return false; }
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount, balance, "Cash deposit"));
        return true;
    }

    // ── Withdraw ─────────────────────────────────────────────────────────────
    public boolean withdraw(double amount) {
        if (!isActive) { System.out.println("  Account is inactive."); return false; }
        if (amount <= 0) { System.out.println("  Amount must be positive."); return false; }

        double minBalance = accountType.equals("SAVINGS") ? SAVINGS_MIN_BALANCE : CHECKING_MIN_BALANCE;
        if (balance - amount < minBalance) {
            System.out.printf("  Insufficient funds. Min balance required: $%.2f%n", minBalance);
            return false;
        }
        balance -= amount;
        transactions.add(new Transaction("WITHDRAWAL", amount, balance, "Cash withdrawal"));
        return true;
    }

    // ── PIN validation ────────────────────────────────────────────────────────
    public boolean validatePin(String pin) {
        return this.pin.equals(pin);
    }

    public boolean changePin(String oldPin, String newPin) {
        if (!validatePin(oldPin)) { System.out.println("  Incorrect current PIN."); return false; }
        if (newPin.length() != 4 || !newPin.matches("\\d+")) {
            System.out.println("  PIN must be 4 digits.");
            return false;
        }
        this.pin = newPin;
        return true;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public String getAccountNumber()     { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getAccountType()       { return accountType; }
    public double getBalance()           { return balance; }
    public boolean isActive()            { return isActive; }
    public List<Transaction> getTransactions() { return transactions; }

    public void setAccountHolderName(String name) { this.accountHolderName = name; }
    public void setActive(boolean active)          { this.isActive = active; }
    public void addTransaction(Transaction t)      { transactions.add(t); }
    public void setBalance(double balance)         { this.balance = balance; }

    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("Account[%s | %s | %s | $%.2f | %s]",
                accountNumber, accountHolderName, accountType, balance,
                isActive ? "Active" : "Inactive");
    }
}
