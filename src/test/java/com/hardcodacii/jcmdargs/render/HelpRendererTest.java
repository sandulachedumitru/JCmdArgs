package com.hardcodacii.jcmdargs.render;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelpRendererTest {

    @Test
    void rendersConciseHelpFromCliSpec() {
        var compilation = new JcaCompiler().compile(
                JcaSource.of(
                        "clone.jca",
                        """
                        command=clone
                        option=!{--help,-h}
                        option={--debug,-d}={disable,enable}
                        option={--long-form,-lf}
                        option=+{--config,-c}=#
                        argument=source=#
                        argument=destination=repository

                        doc.summary="Repository clone utility."
                        doc.command.clone.summary="Clone a repository."
                        doc.option.--help.summary="Show help and exit."
                        doc.option.--debug.summary="Control debug output."
                        doc.option.--long-form.summary="Use the long output form."
                        doc.option.--config.summary="Configuration file to use."
                        doc.argument.source.summary="Source repository."
                        doc.argument.destination.summary="Destination directory."
                        """
                )
        );

        var spec = compilation.cliSpec().orElseThrow();

        String help = new HelpRenderer().render(spec, "jcmdargs-demo");

        assertEquals(
                """
                Repository clone utility.

                Usage: jcmdargs-demo clone [options] <source> [destination]

                Command:
                  clone    Clone a repository.

                Options:
                  -h, --help               Show help and exit. Standalone.
                  -d, --debug [<value>]    Control debug output. Values: disable, enable. Implicit: disable.
                  -lf, --long-form         Use the long output form.
                  -c, --config <value>     Configuration file to use. Required.

                Arguments:
                  source         Source repository. Required.
                  destination    Destination directory. Default: repository.
                """.stripTrailing(),
                help
        );
    }
}
