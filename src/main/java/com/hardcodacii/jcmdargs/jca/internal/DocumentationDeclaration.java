package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;

import java.util.Objects;
import java.util.Optional;

public record DocumentationDeclaration(
        TargetType targetType,
        Optional<String> target,
        Field field,
        String text,
        SourceLocation sourceLocation
) implements JcaStatement {

    public DocumentationDeclaration {
        Objects.requireNonNull(targetType, "targetType");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(sourceLocation, "sourceLocation");
    }

    public enum TargetType {
        ROOT,
        COMMAND,
        OPTION,
        ARGUMENT
    }

    public enum Field {
        SUMMARY,
        DESCRIPTION
    }
}
