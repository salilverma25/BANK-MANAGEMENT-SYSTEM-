package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static int idCounter = 1000;

    private String transactionId;
    private String type;      // DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    private double amount;
    private double balanceAfter;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter, String description) {
        this.transactionId = "TXN" + (++idCounter);
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public String getType()          { return type; }
    public double getAmount()        { return amount; }
    public double getBalanceAfter()  { return balanceAfter; }
    public String getDescription()   { return description; }

    public String getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        String sign = (type.equals("DEPOSIT") || type.equals("TRANSFER_IN")) ? "+" : "-";
        return String.format("  %-12s  %-20s  %s$%-10.2f  Balance: $%.2f",
                transactionId, getFormattedTime().substring(0, 19),
                sign, amount, balanceAfter);
    }
}
