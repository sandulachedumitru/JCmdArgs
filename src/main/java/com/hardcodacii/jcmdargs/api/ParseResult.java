package com.hardcodacii.jcmdargs.api;

import com.hardcodacii.jcmdargs.diagnostic.Diagnostic;
import com.hardcodacii.jcmdargs.runtime.ParsedCommandLine;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ParseResult(
        Optional<ParsedCommandLine> commandLine,
        List<Diagnostic> diagnostics
) {
    public ParseResult {
        Objects.requireNonNull(commandLine, "commandLine");
        Objects.requireNonNull(diagnostics, "diagnostics");

        diagnostics = List.copyOf(diagnostics);

        boolean hasErrors = diagnostics.stream().anyMatch(Diagnostic::isError);

        if (hasErrors && commandLine.isPresent()) {
            throw new IllegalArgumentException(
                    "commandLine must be empty when error diagnostics are present"
            );
        }
        if (!hasErrors && commandLine.isEmpty()) {
            throw new IllegalArgumentException(
                    "commandLine must be present when no error diagnostics are present"
            );
        }
    }

    public static ParseResult success(
            ParsedCommandLine commandLine,
            List<Diagnostic> diagnostics
    ) {
        return new ParseResult(Optional.of(commandLine), diagnostics);
    }

    public static ParseResult failure(List<Diagnostic> diagnostics) {
        return new ParseResult(Optional.empty(), diagnostics);
    }

    public boolean success() {
        return commandLine.isPresent();
    }

    public boolean hasErrors() {
        return diagnostics.stream().anyMatch(Diagnostic::isError);
    }
}
