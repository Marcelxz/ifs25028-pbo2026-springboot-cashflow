package org.delcom.ifs25028pbo2026springbootcashflow.usecase;

import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.util.List;

public class FinanceUseCase {

    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction addIncome(String description, double amount) throws ValidationException {
        return add(description, amount, TransactionType.INCOME);
    }

    public Transaction addExpense(String description, double amount) throws ValidationException {
        return add(description, amount, TransactionType.EXPENSE);
    }

    private Transaction add(String description, double amount, TransactionType type)
            throws ValidationException {
        validate(description, amount);
        return repository.save(description.trim(), amount, type);
    }

    private void validate(String description, double amount) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
    }

    public void removeTransaction(int id) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    public List<Transaction> searchTransactions(String keyword) {
        String lower = keyword.toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(lower));
    }

    public List<Transaction> sortTransactions(SortOption option) {
        return repository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    public double getTotalIncome() {
        return totalOf(TransactionType.INCOME);
    }

    public double getTotalExpense() {
        return totalOf(TransactionType.EXPENSE);
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    private double totalOf(TransactionType type) {
        return repository.findAll().stream()
                .filter(t -> t.getType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}