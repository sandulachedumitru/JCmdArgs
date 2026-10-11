package com.hardcodacii.jcmdargs.model;

import java.util.Objects;
import java.util.Optional;

public record Documentation(
        Optional<String> summary,
        Optional<String> description
) {
    private static final Documentation EMPTY =
            new Documentation(Optional.empty(), Optional.empty());

    public Documentation {
        Objects.requireNonNull(summary, "summary");
        Objects.requireNonNull(description, "description");
    }

    public static Documentation empty() {
        return EMPTY;
    }

    public static Documentation summary(String summary) {
        return new Documentation(Optional.of(summary), Optional.empty());
    }
}
