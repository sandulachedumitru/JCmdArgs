package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;

import java.util.List;
import java.util.Objects;

public record OrderDeclaration(
        List<String> categories,
        SourceLocation sourceLocation
) implements JcaStatement {

    public OrderDeclaration {
        Objects.requireNonNull(categories, "categories");
        Objects.requireNonNull(sourceLocation, "sourceLocation");
        categories = List.copyOf(categories);
    }
}
