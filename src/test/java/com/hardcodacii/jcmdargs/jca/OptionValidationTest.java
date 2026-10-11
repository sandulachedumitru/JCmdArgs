package com.hardcodacii.jcmdargs.jca;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionValidationTest {

    @Test
    void acceptsAtomicMultiCharacterShortAlias() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "alias.jca",
                        "option={--long-form,-lf}"
                )
        );

        assertTrue(result.success());
        assertTrue(
                result.cliSpec()
                        .orElseThrow()
                        .option("-lf")
                        .isPresent()
        );
    }

    @Test
    void rejectsAliasCollisionAcrossOptions() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "collision.jca",
                        """
                        option={--verbose,-v}
                        option={--version,-v}
                        """
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA103",
                result.diagnostics().getFirst().code()
        );
    }

    @Test
    void rejectsOptionAliasWithoutDashPrefix() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "invalid-alias.jca",
                        "option={verbose}"
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA104",
                result.diagnostics().getFirst().code()
        );
    }
    @Test
    void rejectsRequiredStandaloneModifierCombination() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "invalid-modifiers.jca",
                        "option=+!{--help,-h}"
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA108",
                result.diagnostics().getFirst().code()
        );
    }

}
