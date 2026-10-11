package com.hardcodacii.jcmdargs.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Option value policy accepting arbitrary text, optionally with an implicit value.
 */
public record FreeOptionValue(
        Optional<String> implicitValue
) implements OptionValueSpec {

    public FreeOptionValue {
        Objects.requireNonNull(implicitValue, "implicitValue");
    }
}
