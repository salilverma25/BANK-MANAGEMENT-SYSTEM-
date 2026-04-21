package service;

import model.Account;
import model.Transaction;

import java.util.*;
import java.util.stream.Collectors;

public class BankService {

    private Map<String, Account> accounts = new HashMap<>();
    private int accountCounter = 1000;
    private static final String ADMIN_PASSWORD = "admin123";

    public BankService() {
        seedDemoAccounts();
    }

    // ── Admin auth ────────────────────────────────────────────────────────────
    public boolean authenticateAdmin(String password) {
        return ADMIN_PASSWORD.equals(password);
    }

    // ── Create account ────────────────────────────────────────────────────────
    public Account createAccount(String holderName, String type, double initialDeposit, String pin) {
        if (holderName == null || holderName.trim().isEmpty()) {
            System.out.println("  Name cannot be empty.");
            return null;
        }
        if (!List.of("SAVINGS", "CHECKING", "FIXED_DEPOSIT").contains(type)) {
            System.out.println("  Invalid account type.");
            return null;
        }
        double minDeposit = type.equals("SAVINGS") ? 500.0 : (type.equals("FIXED_DEPOSIT") ? 1000.0 : 0.0);
        if (initialDeposit < minDeposit) {
            System.out.printf("  Minimum initial deposit for %s is $%.2f%n", type, minDeposit);
            return null;
        }
        if (pin == null || pin.length() != 4 || !pin.matches("\\d+")) {
            System.out.println("  PIN must be exactly 4 digits.");
            return null;
        }

        String accNum = "ACC" + String.format("%06d", ++accountCounter);
        Account account = new Account(accNum, holderName.trim(), type, initialDeposit, pin);
        accounts.put(accNum, account);
        return account;
    }

    // ── Get account ───────────────────────────────────────────────────────────
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // ── Deposit ───────────────────────────────────────────────────────────────
    public boolean deposit(String accountNumber, double amount) {
        Account acc = findActive(accountNumber);
        if (acc == null) return false;
        return acc.deposit(amount);
    }

    // ── Withdraw ──────────────────────────────────────────────────────────────
    public boolean withdraw(String accountNumber, String pin, double amount) {
        Account acc = findActive(accountNumber);
        if (acc == null) return false;
        if (!acc.validatePin(pin)) { System.out.println("  Invalid PIN."); return false; }
        return acc.withdraw(amount);
    }

    // ── Transfer ──────────────────────────────────────────────────────────────
    public boolean transfer(String fromAccNum, String pin, String toAccNum, double amount) {
        Account from = findActive(fromAccNum);
        Account to   = findActive(toAccNum);

        if (from == null) return false;
        if (to == null)   { System.out.println("  Destination account not found or inactive."); return false; }
        if (fromAccNum.equals(toAccNum)) { System.out.println("  Cannot transfer to same account."); return false; }
        if (!from.validatePin(pin))      { System.out.println("  Invalid PIN."); return false; }

        if (!from.withdraw(amount)) return false;

        // Manually credit destination (bypass PIN)
        to.setBalance(to.getBalance() + amount);
        to.addTransaction(new Transaction("TRANSFER_IN", amount, to.getBalance(),
                "Transfer from " + fromAccNum));

        // Replace the last withdrawal transaction description
        List<Transaction> txns = from.getTransactions();
        // already recorded as WITHDRAWAL; add a note via description override would need constructor change
        // Instead, record an extra marker
        from.addTransaction(new Transaction("TRANSFER_OUT", 0,
                from.getBalance(), "Transfer to " + toAccNum + " marker"));

        return true;
    }

    // ── Balance enquiry ───────────────────────────────────────────────────────
    public double getBalance(String accountNumber, String pin) {
        Account acc = findActive(accountNumber);
        if (acc == null) return -1;
        if (!acc.validatePin(pin)) { System.out.println("  Invalid PIN."); return -1; }
        return acc.getBalance();
    }

    // ── Mini statement ────────────────────────────────────────────────────────
    public List<Transaction> getMiniStatement(String accountNumber, String pin, int count) {
        Account acc = findActive(accountNumber);
        if (acc == null) return null;
        if (!acc.validatePin(pin)) { System.out.println("  Invalid PIN."); return null; }

        List<Transaction> txns = acc.getTransactions();
        int start = Math.max(0, txns.size() - count);
        return txns.subList(start, txns.size());
    }

    // ── Change PIN ────────────────────────────────────────────────────────────
    public boolean changePin(String accountNumber, String oldPin, String newPin) {
        Account acc = findActive(accountNumber);
        if (acc == null) return false;
        return acc.changePin(oldPin, newPin);
    }

    // ── Admin: list all accounts ──────────────────────────────────────────────
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // ── Admin: search ─────────────────────────────────────────────────────────
    public List<Account> searchByName(String name) {
        String lower = name.toLowerCase();
        return accounts.values().stream()
                .filter(a -> a.getAccountHolderName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ── Admin: freeze / unfreeze ──────────────────────────────────────────────
    public boolean setAccountStatus(String accountNumber, boolean active) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) { System.out.println("  Account not found."); return false; }
        acc.setActive(active);
        return true;
    }

    // ── Admin: delete ─────────────────────────────────────────────────────────
    public boolean deleteAccount(String accountNumber) {
        if (!accounts.containsKey(accountNumber)) {
            System.out.println("  Account not found.");
            return false;
        }
        accounts.remove(accountNumber);
        return true;
    }

    // ── Admin: total deposits ─────────────────────────────────────────────────
    public double getTotalDeposits() {
        return accounts.values().stream().mapToDouble(Account::getBalance).sum();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Account findActive(String accountNumber) {
        Account acc = accounts.get(accountNumber);
        if (acc == null)     { System.out.println("  Account not found: " + accountNumber); return null; }
        if (!acc.isActive()) { System.out.println("  Account is frozen/inactive."); return null; }
        return acc;
    }

    // ── Seed data ─────────────────────────────────────────────────────────────
    private void seedDemoAccounts() {
        Account a1 = new Account("ACC001001", "Alice Johnson", "SAVINGS", 5000.00, "1234");
        Account a2 = new Account("ACC001002", "Bob Smith",    "CHECKING", 2000.00, "5678");
        Account a3 = new Account("ACC001003", "Carol White",  "FIXED_DEPOSIT", 10000.00, "9999");

        // Some transactions
        a1.deposit(1500); a1.withdraw(200);
        a2.deposit(300);

        accounts.put("ACC001001", a1);
        accounts.put("ACC001002", a2);
        accounts.put("ACC001003", a3);
    }
}
