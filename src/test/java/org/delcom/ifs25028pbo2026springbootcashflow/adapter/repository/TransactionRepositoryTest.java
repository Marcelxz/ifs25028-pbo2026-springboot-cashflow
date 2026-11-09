package org.delcom.ifs25028pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRepositoryTest {

    private TransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();
    }

    @Test
    void saveGeneratesIncrementalIds() {
        Transaction first = repository.save("Gaji", 1000, TransactionType.INCOME);
        Transaction second = repository.save("Makan", 50, TransactionType.EXPENSE);

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
    }

    @Test
    void saveEntityStoresGivenObject() {
        Transaction manual = new Transaction(99, "Manual", 10, TransactionType.INCOME);

        assertEquals(manual, repository.save(manual));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findAllReturnsDefensiveCopy() {
        repository.save("Gaji", 1000, TransactionType.INCOME);

        List<Transaction> copy = repository.findAll();
        copy.clear();

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findByIdReturnsMatchOrEmpty() {
        repository.save("Gaji", 1000, TransactionType.INCOME);

        assertTrue(repository.findById(1).isPresent());
        assertTrue(repository.findById(42).isEmpty());
    }

    @Test
    void deleteByIdReportsWhetherSomethingWasRemoved() {
        repository.save("Gaji", 1000, TransactionType.INCOME);

        assertTrue(repository.deleteById(1));
        assertFalse(repository.deleteById(1));
    }

    @Test
    void updateKeepsStoredObject() {
        Transaction saved = repository.save("Lama", 100, TransactionType.INCOME);
        saved.changeDescription("Baru");

        repository.update(saved);

        assertEquals("Baru", repository.findById(1).orElseThrow().getDescription());
    }

    @Test
    void findByFiltersWithPredicate() {
        repository.save("Gaji", 1000, TransactionType.INCOME);
        repository.save("Makan", 50, TransactionType.EXPENSE);

        List<Transaction> result = repository.findBy(t -> t.getType() == TransactionType.EXPENSE);

        assertEquals(1, result.size());
        assertEquals("Makan", result.get(0).getDescription());
    }
}