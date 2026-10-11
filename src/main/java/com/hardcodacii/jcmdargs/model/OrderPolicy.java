package com.hardcodacii.jcmdargs.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Required relative order of command, option, and positional argument categories.
 */
public record OrderPolicy(List<ArgumentCategory> categories) {
    public OrderPolicy {
        Objects.requireNonNull(categories, "categories");
        categories = List.copyOf(categories);

        if (categories.isEmpty()) {
            throw new IllegalArgumentException("categories must not be empty");
        }
        if (new HashSet<>(categories).size() != categories.size()) {
            throw new IllegalArgumentException("categories must not contain duplicates");
        }
    }
}
