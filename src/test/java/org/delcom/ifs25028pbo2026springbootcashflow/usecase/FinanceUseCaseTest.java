package org.delcom.ifs25028pbo2026springbootcashflow.usecase;

import org.delcom.ifs25028pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FinanceUseCaseTest {

    private static final double DELTA = 0.001;

    private FinanceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    @Test
    void addIncomeAndExpenseUpdateTotalsAndBalance() throws Exception {
        Transaction income = useCase.addIncome("  Gaji  ", 5_000_000);
        useCase.addExpense("Makan", 25_000);

        assertEquals("Gaji", income.getDescription());
        assertEquals(TransactionType.INCOME, income.getType());
        assertEquals(2, useCase.getAllTransactions().size());
        assertEquals(5_000_000, useCase.getTotalIncome(), DELTA);
        assertEquals(25_000, useCase.getTotalExpense(), DELTA);
        assertEquals(4_975_000, useCase.getBalance(), DELTA);
    }

    @Test
    void emptyDescriptionIsRejected() {
        ValidationException nullEx = assertThrows(ValidationException.class,
                () -> useCase.addIncome(null, 100));
        assertThrows(ValidationException.class, () -> useCase.addExpense("   ", 100));

        assertEquals("Keterangan tidak boleh kosong", nullEx.getMessage());
    }

    @Test
    void invalidAmountIsRejected() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("A", Double.NaN));
        assertThrows(ValidationException.class,
                () -> useCase.addIncome("A", Double.POSITIVE_INFINITY));
        assertThrows(ValidationException.class, () -> useCase.addIncome("A", 0));
        ValidationException negative = assertThrows(ValidationException.class,
                () -> useCase.addExpense("A", -5));

        assertEquals("Jumlah harus lebih besar dari 0", negative.getMessage());
        assertEquals(0, useCase.getAllTransactions().size());
    }

    @Test
    void removeTransactionHandlesAllOutcomes() throws Exception {
        useCase.addIncome("Gaji", 1000);

        ValidationException invalidId = assertThrows(ValidationException.class,
                () -> useCase.removeTransaction(0));
        assertEquals("ID tidak valid", invalidId.getMessage());

        EntityNotFoundException notFound = assertThrows(EntityNotFoundException.class,
                () -> useCase.removeTransaction(99));
        assertEquals(99, notFound.getId());

        useCase.removeTransaction(1);
        assertEquals(0, useCase.getAllTransactions().size());
    }

    @Test
    void searchIsCaseInsensitiveAndDoesNotChangeData() throws Exception {
        useCase.addIncome("Gaji Bulanan", 1000);
        useCase.addExpense("Makan Siang", 50);

        List<Transaction> result = useCase.searchTransactions("GAJI");

        assertEquals(1, result.size());
        assertEquals("Gaji Bulanan", result.get(0).getDescription());
        assertEquals(2, useCase.getAllTransactions().size());
    }

    @Test
    void sortTransactionsUsesSortOptionComparator() throws Exception {
        useCase.addIncome("Kecil", 10);
        useCase.addExpense("Besar", 500);

        List<Transaction> descending = useCase.sortTransactions(SortOption.AMOUNT_DESC);

        assertEquals("Besar", descending.get(0).getDescription());
        assertEquals("Kecil", descending.get(1).getDescription());
    }

    @Test
    void balanceIsNegativeWhenExpenseIsLarger() throws Exception {
        useCase.addIncome("Gaji", 100);
        useCase.addExpense("Laptop", 300);

        assertEquals(-200, useCase.getBalance(), DELTA);
    }
}