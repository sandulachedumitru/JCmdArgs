package com.hardcodacii.jcmdargs.model;

import java.util.Objects;

public record DefaultArgumentValue(
        String defaultValue
) implements ArgumentValueSpec {

    public DefaultArgumentValue {
        Objects.requireNonNull(defaultValue, "defaultValue");
    }
}
