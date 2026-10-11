package com.hardcodacii.jcmdargs.runtime;

import java.util.Objects;
import java.util.Optional;

/**
 * One parsed option occurrence, including the alias matched at runtime.
 */
public record ParsedOption(
        String primaryAlias,
        String matchedAlias,
        Optional<String> value
) {
    public ParsedOption {
        Objects.requireNonNull(primaryAlias, "primaryAlias");
        Objects.requireNonNull(matchedAlias, "matchedAlias");
        Objects.requireNonNull(value, "value");

        if (primaryAlias.isBlank()) {
            throw new IllegalArgumentException("primaryAlias must not be blank");
        }
        if (matchedAlias.isBlank()) {
            throw new IllegalArgumentException("matchedAlias must not be blank");
        }
    }
}
