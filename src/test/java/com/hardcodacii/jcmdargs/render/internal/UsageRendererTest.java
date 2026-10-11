package com.hardcodacii.jcmdargs.render.internal;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsageRendererTest {

    @Test
    void derivesUsageFromSemanticModel() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "usage.jca",
                        """
                        command=clone
                        option={--verbose,-v}
                        argument=source=#
                        argument=destination=repository
                        """
                )
        );

        String usage = new UsageRenderer().render(
                result.cliSpec().orElseThrow(),
                "tool"
        );

        assertEquals(
                "Usage: tool clone [options] <source> [destination]",
                usage
        );
    }

    @Test
    void rejectsBlankProgramName() {
        var result = new JcaCompiler().compile(
                JcaSource.of("usage.jca", "command=status")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new UsageRenderer().render(
                        result.cliSpec().orElseThrow(),
                        " "
                )
        );
    }
}
