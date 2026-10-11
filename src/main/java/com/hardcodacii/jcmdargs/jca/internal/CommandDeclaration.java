package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;

import java.util.Objects;

public record CommandDeclaration(
        String name,
        SourceLocation sourceLocation
) implements JcaStatement {

    public CommandDeclaration {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sourceLocation, "sourceLocation");
    }
}
