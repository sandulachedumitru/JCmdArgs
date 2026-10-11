package com.hardcodacii.jcmdargs.model;

import java.util.Objects;

public record CommandSpec(
        String name,
        Documentation documentation
) {
    public CommandSpec {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(documentation, "documentation");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
