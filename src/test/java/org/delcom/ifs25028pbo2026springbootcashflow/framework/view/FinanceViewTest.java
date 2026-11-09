package org.delcom.ifs25028pbo2026springbootcashflow.framework.view;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.delcom.ifs25028pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25028pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25028pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

class FinanceViewTest {

    private static final double DELTA = 0.001;

    private FinanceUseCase useCase;
    private FinancePresenter presenter;

    @BeforeEach
    void setUp() {
        MockVaadin.setup();
        useCase = new FinanceUseCase(new TransactionRepository());
        presenter = new FinancePresenter();
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    // ---------- helper ----------

    private void mount(FinanceUseCase target) {
        UI.getCurrent().add(new FinanceView(target, presenter));
    }

    @SuppressWarnings("unchecked")
    private Grid<Transaction> grid() {
        return (Grid<Transaction>) _get(Grid.class);
    }

    private int rowCount() {
        return grid().getListDataView().getItemCount();
    }

    private Button button(Component root, String caption) {
        return _get(root, Button.class, spec -> spec.withCaption(caption));
    }

    private Dialog openAddDialog(boolean income, String description, String amount) {
        _click(button(UI.getCurrent(), income ? "Tambah Pemasukan" : "Tambah Pengeluaran"));
        Dialog dialog = _get(Dialog.class);
        _setValue(_get(dialog, TextField.class, spec -> spec.withCaption("Keterangan")), description);
        _setValue(_get(dialog, TextField.class, spec -> spec.withCaption("Jumlah (Rp)")), amount);
        _click(button(dialog, "Simpan"));
        return dialog;
    }

    private void cancel(Dialog dialog) {
        _click(button(dialog, "Batal"));
    }

    private Dialog openDeleteDialog(int rowIndex) {
        Component cell = GridKt._getCellComponent(grid(), rowIndex, "action");
        _click(_get(cell, Button.class));
        return _get(Dialog.class);
    }

    // ---------- test ----------

    @Test
    void addIncomeAndExpenseShowsRowsAndUpdatesBalance() {
        mount(useCase);

        openAddDialog(true, "Gaji", "5000000");
        openAddDialog(false, "Makan", "25000");

        assertEquals(2, rowCount());
        assertEquals(4_975_000, useCase.getBalance(), DELTA);
        assertEquals("1", GridKt._getFormatted(grid(), 0, "id"));
        assertEquals("Gaji", GridKt._getFormatted(grid(), 0, "description"));
        assertEquals("Rp 5,000,000.00", GridKt._getFormatted(grid(), 0, "amount"));
        assertEquals("Pemasukan", GridKt._getFormatted(grid(), 0, "type"));
        assertEquals("Pengeluaran", GridKt._getFormatted(grid(), 1, "type"));
        assertEquals(presenter.formatCreatedAt(useCase.getAllTransactions().get(0)),
                GridKt._getFormatted(grid(), 0, "createdAt"));
    }

    @Test
    void nonNumericAmountKeepsDialogOpenAndAddsNothing() {
        mount(useCase);

        Dialog dialog = openAddDialog(true, "Gaji", "abc");
        cancel(dialog);

        assertEquals(0, useCase.getAllTransactions().size());
    }

    @Test
    void validationErrorsAreHandledInView() {
        mount(useCase);

        Dialog emptyDescription = openAddDialog(true, "", "1000");
        cancel(emptyDescription);
        Dialog negativeAmount = openAddDialog(false, "Makan", "-5");
        cancel(negativeAmount);

        assertEquals(0, useCase.getAllTransactions().size());
    }

    @Test
    void searchFiltersGridAndBlankKeywordRestoresAll() throws Exception {
        useCase.addIncome("Gaji", 1000);
        useCase.addExpense("Makan", 50);
        mount(useCase);
        TextField search = _get(TextField.class, spec -> spec.withPlaceholder("Cari keterangan..."));

        _setValue(search, "gaji");
        _click(button(UI.getCurrent(), "Cari"));
        assertEquals(1, rowCount());

        _setValue(search, "tidak-ada");
        _click(button(UI.getCurrent(), "Cari"));
        assertEquals(0, rowCount());

        _setValue(search, "");
        _click(button(UI.getCurrent(), "Cari"));
        assertEquals(2, rowCount());

        _click(button(UI.getCurrent(), "Muat Ulang"));
        assertEquals(2, rowCount());
    }

    @Test
    void deleteWithConfirmationRemovesTransaction() throws Exception {
        useCase.addIncome("Gaji", 1000);
        mount(useCase);

        Dialog cancelled = openDeleteDialog(0);
        cancel(cancelled);
        assertEquals(1, useCase.getAllTransactions().size());

        Dialog confirmed = openDeleteDialog(0);
        _click(button(confirmed, "Hapus"));

        assertEquals(0, useCase.getAllTransactions().size());
        assertEquals(0, rowCount());
    }

    @Test
    void deleteOfAlreadyRemovedTransactionShowsError() throws Exception {
        useCase.addIncome("Gaji", 1000);
        mount(useCase);

        Dialog dialog = openDeleteDialog(0);
        useCase.removeTransaction(1);
        _click(button(dialog, "Hapus"));

        assertEquals(0, useCase.getAllTransactions().size());
    }

    @Test
    void deleteValidationErrorIsHandledInView() throws Exception {
        FinanceUseCase spied = spy(useCase);
        spied.addIncome("Gaji", 1000);
        mount(spied);
        doThrow(new ValidationException("ID tidak valid")).when(spied).removeTransaction(anyInt());

        Dialog dialog = openDeleteDialog(0);
        _click(button(dialog, "Hapus"));

        assertEquals(1, useCase.getAllTransactions().size());
    }
}