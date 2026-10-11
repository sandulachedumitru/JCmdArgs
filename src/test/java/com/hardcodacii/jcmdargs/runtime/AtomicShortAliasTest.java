package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtomicShortAliasTest {

    @Test
    void multiCharacterShortAliasIsAtomic() {
        var spec = new JcaCompiler()
                .compile(JcaSource.of(
                        "alias.jca",
                        "option={--long-form,-lf}"
                ))
                .cliSpec()
                .orElseThrow();

        var result = new CliParser().parse(spec, "-lf");

        assertTrue(result.success());
        var parsed = result.commandLine().orElseThrow().option("--long-form").orElseThrow();
        assertEquals("-lf", parsed.matchedAlias());
        assertEquals("--long-form", parsed.primaryAlias());
    }
}
