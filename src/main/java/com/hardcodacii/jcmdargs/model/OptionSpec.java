package com.hardcodacii.jcmdargs.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Semantic definition of one logical option and all of its aliases.
 */
public record OptionSpec(
        String primaryAlias,
        List<String> aliases,
        boolean required,
        boolean standalone,
        OptionValueSpec valueSpec,
        Documentation documentation
) {
    public OptionSpec {
        Objects.requireNonNull(primaryAlias, "primaryAlias");
        Objects.requireNonNull(aliases, "aliases");
        Objects.requireNonNull(valueSpec, "valueSpec");
        Objects.requireNonNull(documentation, "documentation");

        aliases = List.copyOf(aliases);

        if (primaryAlias.isBlank()) {
            throw new IllegalArgumentException("primaryAlias must not be blank");
        }
        if (aliases.isEmpty()) {
            throw new IllegalArgumentException("aliases must not be empty");
        }
        if (aliases.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("aliases must not contain null");
        }
        if (aliases.stream().anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("aliases must not contain blank values");
        }
        if (new HashSet<>(aliases).size() != aliases.size()) {
            throw new IllegalArgumentException("aliases must not contain duplicates");
        }
        if (!aliases.contains(primaryAlias)) {
            throw new IllegalArgumentException("primaryAlias must be present in aliases");
        }
        if (required && standalone) {
            throw new IllegalArgumentException(
                    "an option cannot be both required and standalone"
            );
        }
    }
}
