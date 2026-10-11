package com.hardcodacii.jcmdargs.jca;

import com.hardcodacii.jcmdargs.api.CompilationResult;
import com.hardcodacii.jcmdargs.jca.internal.JcaParser;
import com.hardcodacii.jcmdargs.jca.internal.JcaSemanticAnalyzer;

import java.util.Objects;

public final class JcaCompiler {

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
