package org.delcom.ifs25028pbo2026springbootcashflow;

import com.vaadin.flow.server.AppShellSettings;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AppShellTest {

    @Test
    void configuresPageTitle() {
        AppShellSettings settings = mock(AppShellSettings.class);
        new AppShell().configurePage(settings);
        verify(settings).setPageTitle("Catatan Keuangan");
    }
}