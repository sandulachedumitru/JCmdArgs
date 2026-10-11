package com.hardcodacii.jcmdargs.jca;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentationValidationTest {

    @Test
    void resolvesDocumentationByPrimaryOptionAlias() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "docs.jca",
                        """
                        option={--long-form,-lf}
                        doc.option.--long-form.summary="Use the long output form."
                        """
                )
        );

        assertTrue(result.success());

        assertEquals(
                "Use the long output form.",
                result.cliSpec()
                        .orElseThrow()
                        .option("--long-form")
                        .orElseThrow()
                        .documentation()
                        .summary()
                        .orElseThrow()
        );
    }

    @Test
    void rejectsDocumentationForUnknownTarget() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "docs.jca",
                        """
                        option={--debug,-d}
                        doc.option.--debgu.summary="Typo."
                        """
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA501",
                result.diagnostics().getFirst().code()
        );
    }

    @Test
    void supportsMultilineDescriptions() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "docs.jca",
                        "doc.description=\"\"\"\n"
                                + "First line.\n"
                                + "Second line.\n"
                                + "\"\"\"\n"
                )
        );

        assertTrue(result.success());

        assertEquals(
                "First line.\nSecond line.",
                result.cliSpec()
                        .orElseThrow()
                        .documentation()
                        .description()
                        .orElseThrow()
        );
    }
    @Test
    void rejectsDocumentationTargetUsingSecondaryOptionAlias() {
        var result = new JcaCompiler().compile(
                JcaSource.of(
                        "docs.jca",
                        """
                        option={--long-form,-lf}
                        doc.option.-lf.summary="Use the long output form."
                        """
                )
        );

        assertFalse(result.success());
        assertEquals(
                "JCA501",
                result.diagnostics().getFirst().code()
        );
    }

}
