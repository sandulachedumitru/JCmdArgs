package com.hardcodacii.jcmdargs.api;

import com.hardcodacii.jcmdargs.diagnostic.Diagnostic;
import com.hardcodacii.jcmdargs.model.CliSpec;
import com.hardcodacii.jcmdargs.model.Documentation;
import com.hardcodacii.jcmdargs.runtime.ParsedCommandLine;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParseResultTest {

    @Test
    void failureRequiresAnErrorDiagnostic() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ParseResult.failure(
                        List.of(Diagnostic.warning("CLI999", "warning"))
                )
        );
    }

    @Test
    void failedResultHasNoCommandLine() {
        ParseResult result = ParseResult.failure(
                List.of(Diagnostic.error("CLI001", "Unknown option."))
        );

        assertFalse(result.success());
        assertTrue(result.hasErrors());
        assertTrue(result.commandLine().isEmpty());
    }
}
