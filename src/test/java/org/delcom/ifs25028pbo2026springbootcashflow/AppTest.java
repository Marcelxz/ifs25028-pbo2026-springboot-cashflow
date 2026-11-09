package org.delcom.ifs25028pbo2026springbootcashflow;

import org.delcom.ifs25028pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25028pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25028pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository repository;

    @Autowired
    private FinanceUseCase useCase;

    @Autowired
    private FinancePresenter presenter;

    @Test
    void contextLoadsAndBeansAreWired() {
        assertNotNull(repository);
        assertNotNull(useCase);
        assertNotNull(presenter);
    }

    @Test
    void mainDelegatesToSpringApplicationRun() throws Exception {
        try (MockedStatic<SpringApplication> spring = Mockito.mockStatic(SpringApplication.class)) {
            App.main(new String[0]);
            spring.verify(() -> SpringApplication.run(App.class, new String[0]));
        }
    }
}