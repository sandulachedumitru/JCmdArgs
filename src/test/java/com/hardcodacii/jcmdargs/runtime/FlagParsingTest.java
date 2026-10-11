package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlagParsingTest {

    @Test
    void parsesLongAndShortAliasesAsOneLogicalOption() {
        var spec = compile("option={--verbose,-v}");

        var longResult = new CliParser().parse(spec, "--verbose");
        var shortResult = new CliParser().parse(spec, "-v");

        assertTrue(longResult.success());
        assertTrue(shortResult.success());
        assertTrue(longResult.commandLine().orElseThrow().hasOption("-v"));
        assertTrue(shortResult.commandLine().orElseThrow().hasOption("--verbose"));
        assertEquals(
                "-v",
                shortResult.commandLine()
                        .orElseThrow()
                        .option("--verbose")
                        .orElseThrow()
                        .matchedAlias()
        );
    }

    @Test
    void rejectsValueForFlag() {
        var spec = compile("option={--verbose,-v}");
        var result = new CliParser().parse(spec, "--verbose=yes");

        assertFalse(result.success());
        assertEquals("CLI002", result.diagnostics().getFirst().code());
    }

    @Test
    void rejectsUnknownOption() {
        var spec = compile("option={--verbose,-v}");
        var result = new CliParser().parse(spec, "--unknown");

        assertFalse(result.success());
        assertEquals("CLI001", result.diagnostics().getFirst().code());
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
