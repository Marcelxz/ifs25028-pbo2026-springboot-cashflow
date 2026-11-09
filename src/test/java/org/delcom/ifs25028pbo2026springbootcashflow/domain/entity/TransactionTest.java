package org.delcom.ifs25028pbo2026springbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionTest {

    private static final double DELTA = 0.001;

    @Test
    void constructorWithTimeKeepsAllValues() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 9, 10, 30);
        Transaction t = new Transaction(1, "Gaji", 5000, TransactionType.INCOME, time);

        assertEquals(1, t.getId());
        assertEquals("Gaji", t.getDescription());
        assertEquals(5000, t.getAmount(), DELTA);
        assertEquals(TransactionType.INCOME, t.getType());
        assertEquals(time, t.getCreatedAt());
    }

    @Test
    void constructorWithoutTimeSetsCreatedAtAutomatically() {
        Transaction t = new Transaction(2, "Makan", 25, TransactionType.EXPENSE);
        assertNotNull(t.getCreatedAt());
    }

    @Test
    void changeMethodsUpdateValues() {
        Transaction t = new Transaction(1, "Lama", 100, TransactionType.INCOME);
        t.changeDescription("Baru");
        t.changeAmount(250);

        assertEquals("Baru", t.getDescription());
        assertEquals(250, t.getAmount(), DELTA);
    }

    @Test
    void signedAmountDependsOnType() {
        Transaction income = new Transaction(1, "A", 100, TransactionType.INCOME);
        Transaction expense = new Transaction(2, "B", 100, TransactionType.EXPENSE);

        assertEquals(100, income.signedAmount(), DELTA);
        assertEquals(-100, expense.signedAmount(), DELTA);
    }

    @Test
    void transactionTypeHasTwoValues() {
        assertEquals(2, TransactionType.values().length);
        assertEquals(TransactionType.EXPENSE, TransactionType.valueOf("EXPENSE"));
    }
}