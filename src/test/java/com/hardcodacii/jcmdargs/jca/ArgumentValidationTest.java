package com.hardcodacii.jcmdargs.jca;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ArgumentValidationTest {

    @Test
    void rejectsRequiredArgumentAfterOptionalArgument() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "arguments.jca",
                        """
                        argument=destination=?
                        argument=source=#
                        """
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA207",
                result.diagnostics().getFirst().code()
        );
    }
}
