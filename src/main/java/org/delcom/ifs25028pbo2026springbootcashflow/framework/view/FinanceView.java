package org.delcom.ifs25028pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25028pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25028pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.util.Comparator;
import java.util.List;


@Route("")
@Meta(name = "description", content = "Aplikasi web pencatat transaksi keuangan pribadi: catat pemasukan, pengeluaran, dan lihat saldo.")
@PageTitle("Catatan Keuangan")
public class FinanceView extends VerticalLayout {

    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span("- Belum ada transaksi!");

    private final Span totalCount = new Span("0");
    private final Span totalIncome = new Span();
    private final Span totalExpense = new Span();
    private final Span balanceLabel = new Span();

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getElement().setAttribute("role", "main");

        configureGrid();
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setAriaLabel("Cari keterangan");

        add(new H1("Catatan Keuangan"), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    @Override
    protected void onAttach(AttachEvent event) {
        super.onAttach(event);
        event.getUI().getPage().executeJs("document.documentElement.lang = 'id'");
    }

    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                createCard("Jumlah Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E3A8A"),
                createCard("Total Pemasukan", totalIncome, "#DCFCE7", "#16A34A", "#14532D"),
                createCard("Total Pengeluaran", totalExpense, "#FEE2E2", "#DC2626", "#7F1D1D"),
                createCard("Saldo", balanceLabel, "#FEF3C7", "#D97706", "#78350F")
        );
        summary.setWidthFull();
        summary.getStyle().set("align-items", "stretch");
        return summary;
    }

    private Div createCard(String label, Span value, String background, String accent,
                           String textColor) {
        Span caption = new Span(label);
        caption.getStyle()
                .set("display", "block")
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", textColor);

        value.getStyle()
                .set("display", "block")
                .set("margin-top", "0.35rem")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("color", textColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "0")
                .set("background", background)
                .set("border-left", "6px solid " + accent)
                .set("border-radius", "12px")
                .set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        totalIncome.setText(presenter.formatMoney(useCase.getTotalIncome()));
        totalExpense.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceLabel.setText(presenter.formatMoney(useCase.getBalance()));
    }

    private void configureGrid() {
        grid.addColumn(Transaction::getId)
                .setHeader("ID").setKey("id")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(true).setComparator(Transaction::getId);

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan").setKey("description")
                .setFlexGrow(1)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription,
                        String.CASE_INSENSITIVE_ORDER));

        grid.addColumn(t -> presenter.formatMoney(t.getAmount()))
                .setHeader("Jumlah").setKey("amount")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparingDouble(Transaction::getAmount));

        grid.addColumn(t -> presenter.formatType(t.getType()))
                .setHeader("Tipe").setKey("type")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getType));

        grid.addColumn(presenter::formatCreatedAt)
                .setHeader("Waktu").setKey("createdAt")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Tindakan").setKey("action")
                .setAutoWidth(true).setFlexGrow(0)
                .setSortable(false);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
        grid.getElement().setAttribute("aria-label", "Daftar transaksi");
    }

    private HorizontalLayout createActionButtons(Transaction transaction) {
        Button deleteButton = new Button("Hapus", VaadinIcon.TRASH.create(),
                event -> openDeleteDialog(transaction));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(deleteButton);
    }

    private HorizontalLayout buildToolbar() {
        Button incomeButton = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                event -> openAddDialog(TransactionType.INCOME));
        incomeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        Button expenseButton = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                event -> openAddDialog(TransactionType.EXPENSE));
        expenseButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button searchButton = new Button("Cari", VaadinIcon.SEARCH.create(),
                event -> searchTransactions());
        Button refreshButton = new Button("Muat Ulang", VaadinIcon.REFRESH.create(),
                event -> refreshAll());

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField, searchButton, refreshButton, incomeButton, expenseButton);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        return toolbar;
    }

    private void openAddDialog(TransactionType type) {
        boolean income = type == TransactionType.INCOME;

        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();
        TextField amountField = new TextField("Jumlah (Rp)");
        amountField.setWidthFull();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("450px");
        dialog.add(new VerticalLayout(descriptionField, amountField));

        Button saveButton = new Button("Simpan", VaadinIcon.CHECK.create(), event -> {
            double amount;
            try {
                amount = Double.parseDouble(amountField.getValue().trim());
            } catch (NumberFormatException ex) {
                showError(presenter.showInvalidAmount());
                return;
            }
            try {
                Transaction created = income
                        ? useCase.addIncome(descriptionField.getValue(), amount)
                        : useCase.addExpense(descriptionField.getValue(), amount);
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(),
                event -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openDeleteDialog(Transaction transaction) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Hapus Transaksi");
        dialog.add(new Paragraph("Yakin ingin menghapus transaksi \""
                + transaction.getDescription() + "\" (ID " + transaction.getId() + ")?"));

        Button confirmButton = new Button("Hapus", VaadinIcon.TRASH.create(), event -> {
            deleteTransaction(transaction);
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(),
                event -> dialog.close());

        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    private void deleteTransaction(Transaction transaction) {
        try {
            useCase.removeTransaction(transaction.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        } catch (ValidationException ex) {
            showError(presenter.showValidationError(ex.getMessage()));
        }
    }

    private void searchTransactions() {
        String keyword = searchField.getValue();
        if (keyword.isBlank()) {
            refreshAll();
            return;
        }
        List<Transaction> results = useCase.searchTransactions(keyword);
        emptyState.setText(results.isEmpty()
                ? "- Transaksi tidak ditemukan!"
                : "- Belum ada transaksi!");
        refreshGrid(results, "Hasil Pencarian: \"" + keyword + "\"");
    }

    private void refreshAll() {
        emptyState.setText("- Belum ada transaksi!");
        refreshGrid(useCase.getAllTransactions(), "Daftar Transaksi");
        searchField.clear();
    }

    private void refreshGrid(List<Transaction> transactions, String title) {
        grid.setItems(transactions);
        updateSummary();
        statusLabel.setText(transactions.isEmpty()
                ? title + " — " + emptyState.getText().replaceFirst("^- ", "")
                : title);
    }

    private void showSuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}