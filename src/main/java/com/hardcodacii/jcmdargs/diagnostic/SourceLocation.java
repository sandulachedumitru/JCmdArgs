package com.hardcodacii.jcmdargs.diagnostic;

import java.util.Objects;

/**
 * Location of a diagnostic in a named JCA source.
 */
public record SourceLocation(
        String sourceName,
        int line,
        int column
) {
    public SourceLocation {
        Objects.requireNonNull(sourceName, "sourceName");

        if (sourceName.isBlank()) {
            throw new IllegalArgumentException("sourceName must not be blank");
        }
        if (line < 1) {
            throw new IllegalArgumentException("line must be greater than or equal to 1");
        }
        if (column < 1) {
            throw new IllegalArgumentException("column must be greater than or equal to 1");
        }
    }
}
