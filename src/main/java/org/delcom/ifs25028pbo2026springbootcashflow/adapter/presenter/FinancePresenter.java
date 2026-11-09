package org.delcom.ifs25028pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.TransactionType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String formatMoney(double amount) {
        return "Rp " + String.format(Locale.US, "%,.2f", amount);
    }

    public String formatType(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    public String format(Transaction transaction) {
        return String.format("[%d] %s | %s | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                formatMoney(transaction.getAmount()),
                formatType(transaction.getType()));
    }

    public String formatBalance(double balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String formatList(List<Transaction> transactions, String header, String emptyMessage) {
        StringBuilder builder = new StringBuilder(header);
        if (transactions.isEmpty()) {
            builder.append('\n').append(emptyMessage);
            return builder.toString();
        }
        String body = transactions.stream()
                .map(this::format)
                .collect(Collectors.joining("\n"));
        builder.append('\n').append(body);
        return builder.toString();
    }

    public String showTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi:", "- Belum ada transaksi!");
    }

    public String showSearchResults(List<Transaction> transactions, String keyword) {
        return formatList(transactions, "Hasil Pencarian: \"" + keyword + "\"",
                "- Transaksi tidak ditemukan!");
    }

    public String showAddSuccess(Transaction transaction) {
        return "Berhasil menambah transaksi: " + format(transaction);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showInvalidAmount() {
        return "[!] Jumlah harus berupa angka!";
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}