package com.hardcodacii.jcmdargs.model;

import java.util.Objects;
import java.util.Optional;

public record FreeOptionValue(
        Optional<String> implicitValue
) implements OptionValueSpec {

    public FreeOptionValue {
        Objects.requireNonNull(implicitValue, "implicitValue");
    }
}
