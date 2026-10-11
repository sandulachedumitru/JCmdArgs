package com.hardcodacii.jcmdargs.jca;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JcaSyntaxValidationTest {

    @Test
    void ignoresWhitespaceAndCommentLines() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "comments.jca",
                        """

                            # comment
                        command=clone

                        option={--verbose,-v}
                        """
                )
        );

        assertTrue(result.success());
        assertEquals(
                "clone",
                result.cliSpec()
                        .orElseThrow()
                        .command()
                        .orElseThrow()
                        .name()
        );
    }

    @Test
    void rejectsUnknownDefinition() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "unknown.jca",
                        "banana=value"
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA002",
                result.diagnostics().getFirst().code()
        );
    }

    @Test
    void rejectsLegacyUnnamedArgumentSyntax() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "legacy.jca",
                        "argument=source"
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA204",
                result.diagnostics().getFirst().code()
        );
    }
}
