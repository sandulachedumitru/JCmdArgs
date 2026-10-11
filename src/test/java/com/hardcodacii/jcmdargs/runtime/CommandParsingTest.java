package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandParsingTest {

    @Test
    void parsesDeclaredCommand() {
        var spec = compile("command=clone");
        var result = new CliParser().parse(spec, "clone");

        assertTrue(result.success());
        assertEquals("clone", result.commandLine().orElseThrow().command().orElseThrow());
    }

    @Test
    void reportsMissingDeclaredCommand() {
        var spec = compile("command=clone");
        var result = new CliParser().parse(spec);

        assertFalse(result.success());
        assertEquals("CLI103", result.diagnostics().getFirst().code());
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
