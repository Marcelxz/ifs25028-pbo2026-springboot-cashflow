package org.delcom.ifs25028pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

public enum SortOption {

    /** Jumlah terkecil lebih dulu. */
    AMOUNT_ASC(Comparator.comparingDouble(Transaction::getAmount)),

    /** Jumlah terbesar lebih dulu. */
    AMOUNT_DESC(Comparator.comparingDouble(Transaction::getAmount).reversed()),

    /** Pemasukan ditampilkan lebih dulu. */
    INCOME_FIRST(Comparator.comparing(Transaction::getType)),

    /** Pengeluaran ditampilkan lebih dulu. */
    EXPENSE_FIRST(Comparator.comparing(Transaction::getType).reversed());

    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Transaction> comparator() {
        return comparator;
    }
}