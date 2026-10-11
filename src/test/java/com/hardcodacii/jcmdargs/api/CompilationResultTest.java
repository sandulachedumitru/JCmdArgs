package com.hardcodacii.jcmdargs.api;

import com.hardcodacii.jcmdargs.diagnostic.Diagnostic;
import com.hardcodacii.jcmdargs.model.CliSpec;
import com.hardcodacii.jcmdargs.model.Documentation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilationResultTest {

    @Test
    void successfulResultContainsCliSpec() {
        CliSpec spec = emptySpec();

        CompilationResult result = CompilationResult.success(
                spec,
                List.of()
        );

        assertTrue(result.success());
        assertFalse(result.hasErrors());
        assertTrue(result.cliSpec().isPresent());
    }

    @Test
    void failedResultContainsErrorDiagnosticsAndNoCliSpec() {
        CompilationResult result = CompilationResult.failure(
                List.of(Diagnostic.error("JCA001", "Invalid syntax."))
        );

        assertFalse(result.success());
        assertTrue(result.hasErrors());
        assertTrue(result.cliSpec().isEmpty());
    }

    @Test
    void failureRequiresAtLeastOneErrorDiagnostic() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CompilationResult.failure(
                        List.of(Diagnostic.warning("JCA504", "Missing documentation."))
                )
        );
    }

    @Test
    void successfulResultCannotContainErrors() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CompilationResult(
                        Optional.of(emptySpec()),
                        List.of(Diagnostic.error("JCA001", "Invalid syntax."))
                )
        );
    }

    private static CliSpec emptySpec() {
        return new CliSpec(
                Optional.empty(),
                List.of(),
                List.of(),
                Optional.empty(),
                Documentation.empty()
        );
    }
}
