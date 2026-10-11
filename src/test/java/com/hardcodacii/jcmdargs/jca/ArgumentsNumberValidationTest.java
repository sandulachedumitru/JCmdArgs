package com.hardcodacii.jcmdargs.jca;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ArgumentsNumberValidationTest {

    @Test
    void detectsForgottenArgumentDefinition() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "count.jca",
                        """
                        arguments_number=3
                        argument=source=#
                        argument=destination=#
                        """
                )
        );

        assertFalse(result.success());

        assertEquals(
                "JCA203",
                result.diagnostics().getFirst().code()
        );
    }

    @Test
    void acceptsExplicitZeroArguments() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "zero.jca",
                        """
                        arguments_number=0
                        command=status
                        """
                )
        );

        assertEquals(true, result.success());
    }
}
