package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepeatedOptionParsingTest {

    @Test
    void rejectsRepeatedOptionAcrossDifferentAliases() {
        var spec = new JcaCompiler()
                .compile(JcaSource.of(
                        "repeat.jca",
                        "option={--verbose,-v}"
                ))
                .cliSpec()
                .orElseThrow();

        var result = new CliParser().parse(spec, "--verbose", "-v");

        assertFalse(result.success());
        assertTrue(result.diagnostics().stream().anyMatch(d -> d.code().equals("CLI401")));
    }
}
