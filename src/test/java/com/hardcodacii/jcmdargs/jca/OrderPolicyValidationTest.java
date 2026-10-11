package com.hardcodacii.jcmdargs.jca;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderPolicyValidationTest {

    @Test
    void acceptsOrderPolicyContainingUsedCategories() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "order.jca",
                        """
                        allowed_arguments_order={command,option,argument}
                        command=clone
                        option={--verbose,-v}
                        argument=source=#
                        """
                )
        );

        assertTrue(result.success());
        assertEquals(
                3,
                result.cliSpec()
                        .orElseThrow()
                        .orderPolicy()
                        .orElseThrow()
                        .categories()
                        .size()
        );
    }

    @Test
    void rejectsOrderPolicyMissingUsedCategory() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "order.jca",
                        """
                        allowed_arguments_order={command,option}
                        command=clone
                        argument=source=#
                        """
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA403",
                result.diagnostics().getFirst().code()
        );
    }
}
