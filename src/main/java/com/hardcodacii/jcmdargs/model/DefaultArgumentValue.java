package com.hardcodacii.jcmdargs.model;

import java.util.Objects;

/**
 * Positional argument value policy with a default value.
 */
public record DefaultArgumentValue(
        String defaultValue
) implements ArgumentValueSpec {

    public DefaultArgumentValue {
        Objects.requireNonNull(defaultValue, "defaultValue");
    }
}
