package com.hardcodacii.jcmdargs.diagnostic;

import java.util.Objects;
import java.util.Optional;

/**
 * Structured compiler or runtime diagnostic.
 */
public record Diagnostic(
        String code,
        DiagnosticSeverity severity,
        String message,
        Optional<SourceLocation> sourceLocation
) {
    public Diagnostic {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(sourceLocation, "sourceLocation");

        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }

    public static Diagnostic error(String code, String message) {
        return new Diagnostic(code, DiagnosticSeverity.ERROR, message, Optional.empty());
    }

    public static Diagnostic error(
            String code,
            String message,
            SourceLocation sourceLocation
    ) {
        return new Diagnostic(
                code,
                DiagnosticSeverity.ERROR,
                message,
                Optional.of(sourceLocation)
        );
    }

    public static Diagnostic warning(String code, String message) {
        return new Diagnostic(code, DiagnosticSeverity.WARNING, message, Optional.empty());
    }

    public boolean isError() {
        return severity == DiagnosticSeverity.ERROR;
    }
}
