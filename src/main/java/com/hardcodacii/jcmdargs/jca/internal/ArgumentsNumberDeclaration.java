package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;

import java.util.Objects;

public record ArgumentsNumberDeclaration(
        String rawValue,
        SourceLocation sourceLocation
) implements JcaStatement {

    public ArgumentsNumberDeclaration {
        Objects.requireNonNull(rawValue, "rawValue");
        Objects.requireNonNull(sourceLocation, "sourceLocation");
    }
}
