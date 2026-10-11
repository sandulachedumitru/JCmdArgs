package com.hardcodacii.jcmdargs.model;

import java.util.Objects;

public record ArgumentSpec(
        String name,
        int position,
        ArgumentValueSpec valueSpec,
        Documentation documentation
) {
    public ArgumentSpec {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(valueSpec, "valueSpec");
        Objects.requireNonNull(documentation, "documentation");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
    }
}
