package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record OptionDeclaration(
        boolean required,
        boolean standalone,
        List<String> aliases,
        ValueDeclaration value,
        SourceLocation sourceLocation
) implements JcaStatement {

    public OptionDeclaration {
        Objects.requireNonNull(aliases, "aliases");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(sourceLocation, "sourceLocation");
        aliases = List.copyOf(aliases);
    }

    public sealed interface ValueDeclaration
            permits NoValue, FreeValue, EnumValue {
    }

    public record NoValue() implements ValueDeclaration {
    }

    public record FreeValue(Optional<String> implicitValue)
            implements ValueDeclaration {

        public FreeValue {
            Objects.requireNonNull(implicitValue, "implicitValue");
        }
    }

    public record EnumValue(
            List<String> allowedValues,
            Optional<String> implicitValue
    ) implements ValueDeclaration {

        public EnumValue {
            Objects.requireNonNull(allowedValues, "allowedValues");
            Objects.requireNonNull(implicitValue, "implicitValue");
            allowedValues = List.copyOf(allowedValues);
        }
    }
}
