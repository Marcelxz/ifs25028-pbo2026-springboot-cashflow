package org.delcom.ifs25028pbo2026springbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortOptionTest {

    private final Transaction a = new Transaction(1, "A", 300, TransactionType.EXPENSE);
    private final Transaction b = new Transaction(2, "B", 100, TransactionType.INCOME);
    private final Transaction c = new Transaction(3, "C", 200, TransactionType.EXPENSE);

    private List<Integer> sortedIds(SortOption option) {
        return List.of(a, b, c).stream()
                .sorted(option.comparator())
                .map(Transaction::getId)
                .toList();
    }

    @Test
    void amountAscending() {
        assertEquals(List.of(2, 3, 1), sortedIds(SortOption.AMOUNT_ASC));
    }

    @Test
    void amountDescending() {
        assertEquals(List.of(1, 3, 2), sortedIds(SortOption.AMOUNT_DESC));
    }

    @Test
    void incomeFirst() {
        assertEquals(List.of(2, 1, 3), sortedIds(SortOption.INCOME_FIRST));
    }

    @Test
    void expenseFirst() {
        assertEquals(List.of(1, 3, 2), sortedIds(SortOption.EXPENSE_FIRST));
    }
}