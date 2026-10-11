package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionValueParsingTest {

    @Test
    void enumWithImplicitValueUsesImplicitValueWhenNoEqualsValueExists() {
        var spec = compile("option={--debug,-d}={disable,enable}");
        var result = new CliParser().parse(spec, "--debug");

        assertTrue(result.success());
        assertEquals(
                "disable",
                result.commandLine().orElseThrow().optionValue("--debug").orElseThrow()
        );
    }

    @Test
    void enumWithImplicitValueDoesNotConsumeFollowingPositionalToken() {
        var spec = compile("""
                option={--debug,-d}={disable,enable}
                argument=value=#
                """);

        var result = new CliParser().parse(spec, "--debug", "enable");

        assertTrue(result.success());
        assertEquals("disable", result.commandLine().orElseThrow().optionValue("--debug").orElseThrow());
        assertEquals("enable", result.commandLine().orElseThrow().argument("value").orElseThrow());
    }

    @Test
    void enumAcceptsExplicitEqualsValue() {
        var spec = compile("option={--debug,-d}={disable,enable}");
        var result = new CliParser().parse(spec, "--debug=enable");

        assertTrue(result.success());
        assertEquals("enable", result.commandLine().orElseThrow().optionValue("-d").orElseThrow());
    }

    @Test
    void enumWithoutImplicitValueConsumesFollowingValue() {
        var spec = compile("option={--mode,-m}=#{fast,slow}");
        var result = new CliParser().parse(spec, "--mode", "slow");

        assertTrue(result.success());
        assertEquals("slow", result.commandLine().orElseThrow().optionValue("--mode").orElseThrow());
    }

    @Test
    void rejectsInvalidEnumValue() {
        var spec = compile("option={--debug,-d}={disable,enable}");
        var result = new CliParser().parse(spec, "--debug=other");

        assertFalse(result.success());
        assertEquals("CLI003", result.diagnostics().getFirst().code());
    }

    @Test
    void freeRequiredValueSupportsEqualsAndSeparateToken() {
        var spec = compile("option={--output,-o}=#");

        var equalsResult = new CliParser().parse(spec, "--output=a.txt");
        var separateResult = new CliParser().parse(spec, "-o", "b.txt");

        assertEquals("a.txt", equalsResult.commandLine().orElseThrow().optionValue("--output").orElseThrow());
        assertEquals("b.txt", separateResult.commandLine().orElseThrow().optionValue("--output").orElseThrow());
    }

    @Test
    void freeRequiredValueReportsMissingValue() {
        var spec = compile("option={--output,-o}=#");
        var result = new CliParser().parse(spec, "--output");

        assertFalse(result.success());
        assertEquals("CLI004", result.diagnostics().getFirst().code());
    }

    @Test
    void freeImplicitValueDoesNotConsumeFollowingPositionalToken() {
        var spec = compile("""
                option={--print-to,-pt}={Logs.txt,#}
                argument=source=#
                """);

        var result = new CliParser().parse(spec, "--print-to", "source.txt");

        assertTrue(result.success());
        assertEquals("Logs.txt", result.commandLine().orElseThrow().optionValue("--print-to").orElseThrow());
        assertEquals("source.txt", result.commandLine().orElseThrow().argument("source").orElseThrow());
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
