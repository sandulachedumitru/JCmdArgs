package com.hardcodacii.jcmdargs.jca;

import com.hardcodacii.jcmdargs.api.CompilationResult;
import com.hardcodacii.jcmdargs.jca.internal.JcaParser;
import com.hardcodacii.jcmdargs.jca.internal.JcaSemanticAnalyzer;

import java.util.Objects;

/**
 * Compiles JCA source text into an immutable semantic CLI specification.
 */
public final class JcaCompiler {

    /**
     * Parses and semantically validates the supplied JCA source.
     *
     * @param source source to compile
     * @return compilation result
     */
    public CompilationResult compile(JcaSource source) {
        Objects.requireNonNull(source, "source");

        var parseResult = new JcaParser().parse(source);

        if (parseResult.hasErrors()) {
            return CompilationResult.failure(parseResult.diagnostics());
        }

        return new JcaSemanticAnalyzer().analyze(
                parseResult.document(),
                parseResult.diagnostics()
        );
    }
}
