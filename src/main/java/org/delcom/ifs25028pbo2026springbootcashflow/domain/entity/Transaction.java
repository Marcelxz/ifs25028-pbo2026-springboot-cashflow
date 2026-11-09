package org.delcom.ifs25028pbo2026springbootcashflow.domain.entity;

import java.time.LocalDateTime;

public class Transaction {

    private final int id;

    private String description;
    private double amount;
    private final TransactionType type;
    private final LocalDateTime createdAt;

    public Transaction(int id, String description, double amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, double amount, TransactionType type,
                       LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Mengubah keterangan transaksi. */
    public void changeDescription(String description) {
        this.description = description;
    }

    /** Mengubah jumlah uang transaksi. */
    public void changeAmount(double amount) {
        this.amount = amount;
    }

    /** Jumlah bertanda: positif untuk pemasukan, negatif untuk pengeluaran. */
    public double signedAmount() {
        return type == TransactionType.INCOME ? amount : -amount;
    }
}