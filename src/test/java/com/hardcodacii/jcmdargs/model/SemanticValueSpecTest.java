package com.hardcodacii.jcmdargs.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SemanticValueSpecTest {

    @Test
    void enumValueSpecDefensivelyCopiesAllowedValues() {
        List<String> source = new ArrayList<>(List.of("disable", "enable"));

        EnumOptionValue valueSpec = new EnumOptionValue(
                source,
                Optional.of("disable")
        );

        source.add("other");

        assertEquals(List.of("disable", "enable"), valueSpec.allowedValues());
        assertThrows(
                UnsupportedOperationException.class,
                () -> valueSpec.allowedValues().add("other")
        );
    }

    @Test
    void enumImplicitValueMustBelongToAllowedValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new EnumOptionValue(
                        List.of("disable", "enable"),
                        Optional.of("other")
                )
        );
    }
}
