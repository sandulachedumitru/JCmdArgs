package com.hardcodacii.jcmdargs.model;

import java.util.Objects;

/**
 * Semantic definition of the command declared by a JCA contract.
 */
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
