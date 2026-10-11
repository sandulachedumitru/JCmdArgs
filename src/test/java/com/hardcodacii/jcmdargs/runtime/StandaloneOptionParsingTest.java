package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandaloneOptionParsingTest {

    @Test
    void standaloneOptionBypassesCommandRequiredOptionAndRequiredArgument() {
        var spec = compile("""
                command=clone
                option=!{--help,-h}
                option=+{--config,-c}=#
                argument=source=#
                """);

        var result = new CliParser().parse(spec, "--help");

        assertTrue(result.success(), () -> result.diagnostics().toString());
        assertTrue(result.commandLine().orElseThrow().hasOption("--help"));
    }

    @Test
    void standaloneOptionMayBeUsedWithItsCommand() {
        var spec = compile("""
                command=clone
                option=!{--help,-h}
                argument=source=#
                """);

        var result = new CliParser().parse(spec, "clone", "--help");

        assertTrue(result.success(), () -> result.diagnostics().toString());
        assertEquals("clone", result.commandLine().orElseThrow().command().orElseThrow());
    }

    @Test
    void standaloneOptionConflictsWithAnotherOption() {
        var spec = compile("""
                option=!{--help,-h}
                option={--verbose,-v}
                """);

        var result = new CliParser().parse(spec, "--help", "--verbose");

        assertFalse(result.success());
        assertTrue(result.diagnostics().stream().anyMatch(d -> d.code().equals("CLI201")));
    }

    @Test
    void standaloneOptionConflictsWithPositionalArgument() {
        var spec = compile("""
                option=!{--help,-h}
                argument=source=#
                """);

        var result = new CliParser().parse(spec, "--help", "file.txt");

        assertFalse(result.success());
        assertTrue(result.diagnostics().stream().anyMatch(d -> d.code().equals("CLI201")));
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
