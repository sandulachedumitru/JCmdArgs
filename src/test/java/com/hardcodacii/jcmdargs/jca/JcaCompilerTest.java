package com.hardcodacii.jcmdargs.jca;

import com.hardcodacii.jcmdargs.model.DefaultArgumentValue;
import com.hardcodacii.jcmdargs.model.EnumOptionValue;
import com.hardcodacii.jcmdargs.model.FreeOptionValue;
import com.hardcodacii.jcmdargs.model.NoOptionValue;
import com.hardcodacii.jcmdargs.model.RequiredArgumentValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JcaCompilerTest {

    @Test
    void compilesRepresentativeJcaContract() {
        String jca = """
                arguments_number=2
                allowed_arguments_order={command,option,argument}

                command=clone

                option=!{--help,-h}
                option={--debug,-d}={disable,enable}
                option={--long-form,-lf}
                option=+{--config,-c}=#
                option={--print-to,-pt}={Logs.txt,#}

                argument=source=#
                argument=destination=repository

                doc.summary="Repository clone utility."
                doc.command.clone.summary="Clone a repository."
                doc.option.--long-form.summary="Use the long output form."
                doc.argument.source.summary="Source repository or directory."
                """;

        var result = new JcaCompiler().compile(
                JcaSource.of("clone.jca", jca)
        );

        assertTrue(result.success(), () -> result.diagnostics().toString());

        var spec = result.cliSpec().orElseThrow();

        assertEquals("clone", spec.command().orElseThrow().name());
        assertEquals(5, spec.options().size());
        assertEquals(2, spec.arguments().size());

        assertInstanceOf(
                NoOptionValue.class,
                spec.option("--help").orElseThrow().valueSpec()
        );

        EnumOptionValue debug = assertInstanceOf(
                EnumOptionValue.class,
                spec.option("--debug").orElseThrow().valueSpec()
        );
        assertEquals("disable", debug.implicitValue().orElseThrow());

        assertEquals(
                spec.option("--long-form").orElseThrow(),
                spec.option("-lf").orElseThrow()
        );

        FreeOptionValue config = assertInstanceOf(
                FreeOptionValue.class,
                spec.option("--config").orElseThrow().valueSpec()
        );
        assertTrue(config.implicitValue().isEmpty());
        assertTrue(spec.option("--config").orElseThrow().required());

        FreeOptionValue printTo = assertInstanceOf(
                FreeOptionValue.class,
                spec.option("--print-to").orElseThrow().valueSpec()
        );
        assertEquals("Logs.txt", printTo.implicitValue().orElseThrow());

        assertInstanceOf(
                RequiredArgumentValue.class,
                spec.argument("source").orElseThrow().valueSpec()
        );

        DefaultArgumentValue destination = assertInstanceOf(
                DefaultArgumentValue.class,
                spec.argument("destination").orElseThrow().valueSpec()
        );
        assertEquals("repository", destination.defaultValue());

        assertEquals(
                "Use the long output form.",
                spec.option("--long-form")
                        .orElseThrow()
                        .documentation()
                        .summary()
                        .orElseThrow()
        );
    }

    @Test
    void quotedHashIsAnArgumentDefaultNotRequiredMarker() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "hash.jca",
                        "argument=value=\"#\""
                )
        );

        assertTrue(result.success());

        DefaultArgumentValue value = assertInstanceOf(
                DefaultArgumentValue.class,
                result.cliSpec()
                        .orElseThrow()
                        .argument("value")
                        .orElseThrow()
                        .valueSpec()
        );

        assertEquals("#", value.defaultValue());
    }
}
