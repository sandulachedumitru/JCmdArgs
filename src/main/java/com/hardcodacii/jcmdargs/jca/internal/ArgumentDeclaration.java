package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;

import java.util.Objects;

public record ArgumentDeclaration(
        String name,
        ValueDeclaration value,
        SourceLocation sourceLocation
) implements JcaStatement {

    public ArgumentDeclaration {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(sourceLocation, "sourceLocation");
    }

    public sealed interface ValueDeclaration
            permits RequiredValue, OptionalValue, DefaultValue {
    }

    public record RequiredValue() implements ValueDeclaration {
    }

    public record OptionalValue() implements ValueDeclaration {
    }

    public record DefaultValue(String value) implements ValueDeclaration {
        public DefaultValue {
            Objects.requireNonNull(value, "value");
        }
    }
}
