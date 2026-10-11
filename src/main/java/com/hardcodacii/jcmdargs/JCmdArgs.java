package com.hardcodacii.jcmdargs;

import com.hardcodacii.jcmdargs.api.CompilationResult;
import com.hardcodacii.jcmdargs.api.ParseResult;
import com.hardcodacii.jcmdargs.jca.JcaCompiler;
import com.hardcodacii.jcmdargs.jca.JcaSource;
import com.hardcodacii.jcmdargs.model.CliSpec;
import com.hardcodacii.jcmdargs.render.HelpRenderer;
import com.hardcodacii.jcmdargs.render.ManualRenderer;
import com.hardcodacii.jcmdargs.runtime.CliParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class JCmdArgs {

    private static final String IN_MEMORY_SOURCE_NAME = "<memory>";

    private JCmdArgs() {
    }

    public static CompilationResult compile(String content) {
        Objects.requireNonNull(content, "content");
        return compile(JcaSource.of(IN_MEMORY_SOURCE_NAME, content));
    }

    public static CompilationResult compile(String sourceName, String content) {
        Objects.requireNonNull(sourceName, "sourceName");
        Objects.requireNonNull(content, "content");
        return compile(JcaSource.of(sourceName, content));
    }

    public static CompilationResult compile(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return compile(JcaSource.from(path));
    }

    public static CompilationResult compile(JcaSource source) {
        Objects.requireNonNull(source, "source");
        return new JcaCompiler().compile(source);
    }

    public static ParseResult parse(CliSpec spec, String... args) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(args, "args");
        return new CliParser().parse(spec, args);
    }

    public static String help(CliSpec spec) {
        Objects.requireNonNull(spec, "spec");
        return new HelpRenderer().render(spec);
    }

    public static String help(CliSpec spec, String programName) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(programName, "programName");
        return new HelpRenderer().render(spec, programName);
    }

    public static String manual(CliSpec spec) {
        Objects.requireNonNull(spec, "spec");
        return new ManualRenderer().render(spec);
    }

    public static String manual(CliSpec spec, String programName) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(programName, "programName");
        return new ManualRenderer().render(spec, programName);
    }
}
