package com.hardcodacii.jcmdargs.diagnostic;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiagnosticTest {

    @Test
    void identifiesErrorSeverity() {
        Diagnostic diagnostic = new Diagnostic(
                "JCA001",
                DiagnosticSeverity.ERROR,
                "Invalid syntax.",
                Optional.of(new SourceLocation("test.jca", 3, 7))
        );

        assertTrue(diagnostic.isError());
    }

    @Test
    void warningIsNotAnError() {
        Diagnostic diagnostic = Diagnostic.warning(
                "JCA504",
                "Missing documentation."
        );

        assertFalse(diagnostic.isError());
    }
}
