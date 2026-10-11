package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequiredOptionParsingTest {

    @Test
    void acceptsRequiredOptionWhenPresent() {
        var spec = compile("option=+{--config,-c}=#");
        var result = new CliParser().parse(spec, "-c", "app.conf");

        assertTrue(result.success());
        assertEquals("app.conf", result.commandLine().orElseThrow().optionValue("--config").orElseThrow());
    }

    @Test
    void reportsMissingRequiredOption() {
        var spec = compile("option=+{--config,-c}=#");
        var result = new CliParser().parse(spec);

        assertFalse(result.success());
        assertEquals("CLI005", result.diagnostics().getFirst().code());
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
