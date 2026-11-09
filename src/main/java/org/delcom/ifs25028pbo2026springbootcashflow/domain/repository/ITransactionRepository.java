package org.delcom.ifs25028pbo2026springbootcashflow.domain.repository;

import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.TransactionType;

public interface ITransactionRepository extends IRepository<Transaction, Integer> {

    /**
     * Menyimpan transaksi baru.
     *
     * @return transaksi yang tersimpan (sudah punya ID)
     */
    Transaction save(String description, double amount, TransactionType type);
}