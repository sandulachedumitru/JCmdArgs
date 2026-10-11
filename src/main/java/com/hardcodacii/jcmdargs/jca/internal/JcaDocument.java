package com.hardcodacii.jcmdargs.jca.internal;

import java.util.List;
import java.util.Objects;

public record JcaDocument(List<JcaStatement> statements) {

    public JcaDocument {
        Objects.requireNonNull(statements, "statements");
        statements = List.copyOf(statements);
    }
}
