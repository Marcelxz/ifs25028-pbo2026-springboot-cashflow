package org.delcom.ifs25028pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancePresenterTest {

    private final FinancePresenter presenter = new FinancePresenter();

    private final Transaction income = new Transaction(1, "Gaji", 1_000_000, TransactionType.INCOME,
            LocalDateTime.of(2026, 11, 9, 10, 30));
    private final Transaction expense = new Transaction(2, "Makan", 25_000, TransactionType.EXPENSE,
            LocalDateTime.of(2026, 11, 9, 12, 0));

    @Test
    void formatMoneyUsesThousandsSeparatorAndTwoDecimals() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(1_000_000));
        assertEquals("Rp 0.50", presenter.formatMoney(0.5));
    }

    @Test
    void formatTypeReturnsIndonesianLabels() {
        assertEquals("Pemasukan", presenter.formatType(TransactionType.INCOME));
        assertEquals("Pengeluaran", presenter.formatType(TransactionType.EXPENSE));
    }

    @Test
    void formatCreatedAtUsesDayMonthYearPattern() {
        assertEquals("09/11/2026 10:30", presenter.formatCreatedAt(income));
    }

    @Test
    void formatBuildsSingleLine() {
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(income));
        assertEquals("[2] Makan | Rp 25,000.00 | [Pengeluaran]", presenter.format(expense));
    }

    @Test
    void formatBalanceShowsSaldoLabel() {
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(4_975_000));
    }

    @Test
    void formatListHandlesEmptyAndFilledLists() {
        assertEquals("Header\nKosong", presenter.formatList(List.of(), "Header", "Kosong"));
        assertEquals("Header\n" + presenter.format(income) + "\n" + presenter.format(expense),
                presenter.formatList(List.of(income, expense), "Header", "Kosong"));
    }

    @Test
    void showTransactionsAndSearchResults() {
        assertEquals("Daftar Transaksi:\n- Belum ada transaksi!", presenter.showTransactions(List.of()));
        assertEquals("Hasil Pencarian: \"gaji\"\n- Transaksi tidak ditemukan!",
                presenter.showSearchResults(List.of(), "gaji"));
    }

    @Test
    void messagesAreFormattedAsExpected() {
        assertEquals("Berhasil menambah transaksi: " + presenter.format(income),
                presenter.showAddSuccess(income));
        assertEquals("Berhasil menghapus transaksi.", presenter.showRemoveSuccess());
        assertEquals("[!] Gagal menghapus transaksi dengan ID: 3.", presenter.showRemoveFailed(3));
        assertEquals("[!] Jumlah harus berupa angka!", presenter.showInvalidAmount());
        assertEquals("[!] pesan", presenter.showValidationError("pesan"));
    }
}