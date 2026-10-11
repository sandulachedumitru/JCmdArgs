package com.hardcodacii.jcmdargs.render;

import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManualRendererTest {

    @Test
    void rendersDescriptionsAndStructuralMetadata() {
        var compilation = new JcaCompiler().compile(
                JcaSource.of(
                        "clone.jca",
                        "command=clone\n"
                                + "option={--debug,-d}={disable,enable}\n"
                                + "argument=source=#\n"
                                + "doc.summary=\"Repository clone utility.\"\n"
                                + "doc.description=\"\"\"\n"
                                + "Top-level description.\n"
                                + "\"\"\"\n"
                                + "doc.command.clone.description=\"Clone command details.\"\n"
                                + "doc.option.--debug.description=\"\"\"\n"
                                + "Controls diagnostic verbosity.\n"
                                + "Useful when troubleshooting.\n"
                                + "\"\"\"\n"
                                + "doc.argument.source.description=\"Repository to clone.\"\n"
                )
        );

        var spec = compilation.cliSpec().orElseThrow();

        String manual = new ManualRenderer().render(spec, "jcmdargs-demo");

        assertTrue(manual.contains("Usage: jcmdargs-demo clone [options] <source>"));
        assertTrue(manual.contains("Controls diagnostic verbosity."));
        assertTrue(manual.contains("Useful when troubleshooting."));
        assertTrue(manual.contains("Values: disable, enable. Implicit: disable."));
        assertTrue(manual.contains("Repository to clone."));
        assertTrue(manual.contains("Required."));
    }

    @Test
    void doesNotWriteDocumentationThatIsNotPresent() {
        var compilation = new JcaCompiler().compile(
                JcaSource.of(
                        "minimal.jca",
                        "option={--verbose,-v}"
                )
        );

        String manual = new ManualRenderer().render(
                compilation.cliSpec().orElseThrow()
        );

        assertEquals(
                """
                Usage: [options]

                Options:
                  -v, --verbose
                """.stripTrailing(),
                manual
        );
    }
}
