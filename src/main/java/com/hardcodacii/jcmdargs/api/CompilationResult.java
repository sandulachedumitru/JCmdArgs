package com.hardcodacii.jcmdargs.api;

import com.hardcodacii.jcmdargs.diagnostic.Diagnostic;
import com.hardcodacii.jcmdargs.model.CliSpec;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record CompilationResult(
        Optional<CliSpec> cliSpec,
        List<Diagnostic> diagnostics
) {
    public CompilationResult {
        Objects.requireNonNull(cliSpec, "cliSpec");
        Objects.requireNonNull(diagnostics, "diagnostics");

        diagnostics = List.copyOf(diagnostics);

        boolean hasErrors = diagnostics.stream().anyMatch(Diagnostic::isError);

        if (hasErrors && cliSpec.isPresent()) {
            throw new IllegalArgumentException(
                    "cliSpec must be empty when error diagnostics are present"
            );
        }
        if (!hasErrors && cliSpec.isEmpty()) {
            throw new IllegalArgumentException(
                    "cliSpec must be present when no error diagnostics are present"
            );
        }
    }

    public static CompilationResult success(
            CliSpec cliSpec,
            List<Diagnostic> diagnostics
    ) {
        return new CompilationResult(Optional.of(cliSpec), diagnostics);
    }

    public static CompilationResult failure(List<Diagnostic> diagnostics) {
        return new CompilationResult(Optional.empty(), diagnostics);
    }

    public boolean success() {
        return cliSpec.isPresent();
    }

    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(Diagnostic::isError);
    }
}
