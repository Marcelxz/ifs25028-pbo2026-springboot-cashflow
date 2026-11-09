package org.delcom.ifs25028pbo2026springbootcashflow.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class DomainExceptionTest {

    @Test
    void domainExceptionKeepsMessageAndCause() {
        Throwable cause = new RuntimeException("penyebab");

        DomainException withMessage = new DomainException("pesan");
        DomainException withCause = new DomainException("pesan", cause);

        assertEquals("pesan", withMessage.getMessage());
        assertSame(cause, withCause.getCause());
    }

    @Test
    void entityNotFoundExceptionKeepsId() {
        EntityNotFoundException ex = new EntityNotFoundException(5);

        assertEquals(5, ex.getId());
        assertEquals("Transaksi dengan ID 5 tidak ditemukan.", ex.getMessage());
        assertInstanceOf(DomainException.class, ex);
    }

    @Test
    void validationExceptionKeepsMessage() {
        ValidationException ex = new ValidationException("tidak valid");

        assertEquals("tidak valid", ex.getMessage());
        assertInstanceOf(DomainException.class, ex);
    }
}