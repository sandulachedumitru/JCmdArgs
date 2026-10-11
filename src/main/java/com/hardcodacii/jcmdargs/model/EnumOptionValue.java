package com.hardcodacii.jcmdargs.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record EnumOptionValue(
        List<String> allowedValues,
        Optional<String> implicitValue
) implements OptionValueSpec {

    public EnumOptionValue {
        Objects.requireNonNull(allowedValues, "allowedValues");
        Objects.requireNonNull(implicitValue, "implicitValue");

        allowedValues = List.copyOf(allowedValues);

        if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("allowedValues must not be empty");
        }
        if (allowedValues.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("allowedValues must not contain null");
        }
        if (allowedValues.stream().anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("allowedValues must not contain blank values");
        }
        if (new HashSet<>(allowedValues).size() != allowedValues.size()) {
            throw new IllegalArgumentException("allowedValues must not contain duplicates");
        }
        if (implicitValue.isPresent() && !allowedValues.contains(implicitValue.get())) {
            throw new IllegalArgumentException(
                    "implicitValue must be one of allowedValues"
            );
        }
    }
}
