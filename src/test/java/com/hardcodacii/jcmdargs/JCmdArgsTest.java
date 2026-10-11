package com.hardcodacii.jcmdargs;

import com.hardcodacii.jcmdargs.model.DefaultArgumentValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JCmdArgsTest {

    private static final String CONTRACT = """
            arguments_number=2
            allowed_arguments_order={command,option,argument}

            command=clone

            option=!{--help,-h}
            option={--debug,-d}={disable,enable}
            option={--long-form,-lf}
            option=+{--config,-c}=#

            argument=source=#
            argument=destination=repository

            doc.summary="Repository clone utility."
            doc.option.--long-form.summary="Use the long output form."
            doc.argument.source.summary="Source repository."
            """;

    @Test
    void facadeCompilesParsesAndRendersFromOneContract() {
        var compilation = JCmdArgs.compile("clone.jca", CONTRACT);

        assertTrue(compilation.success(), () -> compilation.diagnostics().toString());

        var spec = compilation.cliSpec().orElseThrow();

        var parseResult = JCmdArgs.parse(
                spec,
                "clone",
                "-lf",
                "--config",
                "application.properties",
                "https://example.test/repository.git"
        );

        assertTrue(parseResult.success(), () -> parseResult.diagnostics().toString());

        var commandLine = parseResult.commandLine().orElseThrow();

        assertEquals("clone", commandLine.command().orElseThrow());
        assertTrue(commandLine.hasOption("--long-form"));
        assertTrue(commandLine.hasOption("-lf"));
        assertEquals(
                "application.properties",
                commandLine.optionValue("--config").orElseThrow()
        );
        assertEquals(
                "https://example.test/repository.git",
                commandLine.argument("source").orElseThrow()
        );
        assertEquals(
                "repository",
                commandLine.argument("destination").orElseThrow()
        );

        String help = JCmdArgs.help(spec, "jcmdargs-demo");
        assertTrue(help.contains("Usage: jcmdargs-demo clone"));
        assertTrue(help.contains("-lf, --long-form"));
        assertTrue(help.contains("Use the long output form."));

        String manual = JCmdArgs.manual(spec, "jcmdargs-demo");
        assertTrue(manual.contains("Repository clone utility."));
        assertTrue(manual.contains("Source repository."));
    }

    @Test
    void facadeCanCompileAnInMemoryContractWithoutExplicitSourceName() {
        var compilation = JCmdArgs.compile("argument=source=#");

        assertTrue(compilation.success());
        assertTrue(
                compilation.cliSpec()
                        .orElseThrow()
                        .argument("source")
                        .isPresent()
        );
    }

    @Test
    void facadeCanLoadContractFromPath(@TempDir Path tempDir) throws IOException {
        Path contract = tempDir.resolve("tool.jca");
        Files.writeString(contract, """
                argument=destination=repository
                """);

        var compilation = JCmdArgs.compile(contract);

        assertTrue(compilation.success());

        var valueSpec = compilation.cliSpec()
                .orElseThrow()
                .argument("destination")
                .orElseThrow()
                .valueSpec();

        assertEquals(
                "repository",
                ((DefaultArgumentValue) valueSpec).defaultValue()
        );
    }
}
