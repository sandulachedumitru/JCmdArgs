package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PositionalArgumentParsingTest {

    @Test
    void bindsPositionalValuesByDeclarationOrder() {
        var spec = compile("""
                argument=source=#
                argument=destination=repository
                """);

        var result = new CliParser().parse(spec, "src", "dst");

        assertTrue(result.success());
        assertEquals("src", result.commandLine().orElseThrow().argument("source").orElseThrow());
        assertEquals("dst", result.commandLine().orElseThrow().argument("destination").orElseThrow());
    }

    @Test
    void suppliesArgumentDefaultWhenValueIsMissing() {
        var spec = compile("""
                argument=source=#
                argument=destination=repository
                """);

        var result = new CliParser().parse(spec, "src");

        assertTrue(result.success());
        assertEquals("repository", result.commandLine().orElseThrow().argument("destination").orElseThrow());
    }

    @Test
    void optionalArgumentMayBeAbsent() {
        var spec = compile("argument=destination=?");
        var result = new CliParser().parse(spec);

        assertTrue(result.success());
        assertTrue(result.commandLine().orElseThrow().argument("destination").isEmpty());
    }

    @Test
    void reportsMissingRequiredArgument() {
        var spec = compile("argument=source=#");
        var result = new CliParser().parse(spec);

        assertFalse(result.success());
        assertEquals("CLI101", result.diagnostics().getFirst().code());
    }

    @Test
    void reportsTooManyPositionalArguments() {
        var spec = compile("argument=source=#");
        var result = new CliParser().parse(spec, "a", "b");

        assertFalse(result.success());
        assertEquals("CLI102", result.diagnostics().getFirst().code());
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
