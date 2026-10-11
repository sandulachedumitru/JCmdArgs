package com.hardcodacii.jcmdargs.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliSpecTest {

    @Test
    void resolvesAllDeclaredAliasesToTheSameOption() {
        OptionSpec option = new OptionSpec(
                "--long-form",
                List.of("--long-form", "-lf"),
                false,
                false,
                new NoOptionValue(),
                Documentation.empty()
        );

        CliSpec spec = new CliSpec(
                Optional.empty(),
                List.of(option),
                List.of(),
                Optional.empty(),
                Documentation.empty()
        );

        assertSame(option, spec.option("--long-form").orElseThrow());
        assertSame(option, spec.option("-lf").orElseThrow());
        assertTrue(spec.option("-l").isEmpty());
    }

    @Test
    void semanticListsAreImmutableAndDefensivelyCopied() {
        List<OptionSpec> source = new ArrayList<>();

        CliSpec spec = new CliSpec(
                Optional.empty(),
                source,
                List.of(),
                Optional.empty(),
                Documentation.empty()
        );

        source.add(new OptionSpec(
                "--verbose",
                List.of("--verbose", "-v"),
                false,
                false,
                new NoOptionValue(),
                Documentation.empty()
        ));

        assertTrue(spec.options().isEmpty());
        assertThrows(
                UnsupportedOperationException.class,
                () -> spec.options().add(source.getFirst())
        );
    }

    @Test
    void positionalArgumentsMustFollowContiguousDeclarationOrder() {
        ArgumentSpec argument = new ArgumentSpec(
                "source",
                1,
                new RequiredArgumentValue(),
                Documentation.empty()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CliSpec(
                        Optional.empty(),
                        List.of(),
                        List.of(argument),
                        Optional.empty(),
                        Documentation.empty()
                )
        );
    }

    @Test
    void argumentCanBeResolvedByName() {
        ArgumentSpec argument = new ArgumentSpec(
                "source",
                0,
                new RequiredArgumentValue(),
                Documentation.empty()
        );

        CliSpec spec = new CliSpec(
                Optional.empty(),
                List.of(),
                List.of(argument),
                Optional.empty(),
                Documentation.empty()
        );

        assertEquals(argument, spec.argument("source").orElseThrow());
    }
}
