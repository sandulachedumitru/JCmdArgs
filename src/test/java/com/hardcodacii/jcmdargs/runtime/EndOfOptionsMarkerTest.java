package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndOfOptionsMarkerTest {

    @Test
    void treatsDashPrefixedTokenAfterMarkerAsPositionalValue() {
        var spec = compile("""
                option={--verbose,-v}
                argument=value=#
                """);

        var result = new CliParser().parse(spec, "--", "--verbose");

        assertTrue(result.success());
        assertFalse(result.commandLine().orElseThrow().hasOption("--verbose"));
        assertEquals("--verbose", result.commandLine().orElseThrow().argument("value").orElseThrow());
    }

    @Test
    void optionBeforeMarkerRemainsAnOption() {
        var spec = compile("""
                option={--verbose,-v}
                argument=value=#
                """);

        var result = new CliParser().parse(spec, "-v", "--", "-1");

        assertTrue(result.success());
        assertTrue(result.commandLine().orElseThrow().hasOption("--verbose"));
        assertEquals("-1", result.commandLine().orElseThrow().argument("value").orElseThrow());
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
