package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArgumentOrderParsingTest {

    @Test
    void strictOrderAcceptsCommandOptionArgument() {
        var spec = compile("""
                allowed_arguments_order={command,option,argument}
                command=clone
                option={--verbose,-v}
                argument=source=#
                """);

        var result = new CliParser().parse(spec, "clone", "--verbose", "src");

        assertTrue(result.success(), () -> result.diagnostics().toString());
    }

    @Test
    void strictOrderRejectsOptionAfterArgument() {
        var spec = compile("""
                allowed_arguments_order={command,option,argument}
                command=clone
                option={--verbose,-v}
                argument=source=#
                """);

        var result = new CliParser().parse(spec, "clone", "src", "--verbose");

        assertFalse(result.success());
        assertTrue(result.diagnostics().stream().anyMatch(d -> d.code().equals("CLI301")));
    }

    @Test
    void missingOrderPolicyAllowsFlexibleOrder() {
        var spec = compile("""
                command=clone
                option={--verbose,-v}
                argument=source=#
                """);

        var result = new CliParser().parse(spec, "src", "--verbose", "clone");

        assertTrue(result.success(), () -> result.diagnostics().toString());
    }

    private static com.hardcodacii.jcmdargs.model.CliSpec compile(String jca) {
        return new JcaCompiler()
                .compile(JcaSource.of("test.jca", jca))
                .cliSpec()
                .orElseThrow();
    }
}
