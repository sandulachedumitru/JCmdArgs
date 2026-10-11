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

/**
 * Primary static facade for compiling JCA contracts, parsing command lines, and rendering documentation.
 */
public final class JCmdArgs {

    private static final String IN_MEMORY_SOURCE_NAME = "<memory>";

    private JCmdArgs() {
    }

    /**
     * Compiles in-memory JCA content using {@code <memory>} as its source name.
     *
     * @param content JCA source content
     * @return compilation result containing either a {@link CliSpec} or error diagnostics
     */
    public static CompilationResult compile(String content) {
        Objects.requireNonNull(content, "content");
        return compile(JcaSource.of(IN_MEMORY_SOURCE_NAME, content));
    }

    /**
     * Compiles in-memory JCA content with an explicit source name for diagnostics.
     *
     * @param sourceName logical source name
     * @param content JCA source content
     * @return compilation result
     */
    public static CompilationResult compile(String sourceName, String content) {
        Objects.requireNonNull(sourceName, "sourceName");
        Objects.requireNonNull(content, "content");
        return compile(JcaSource.of(sourceName, content));
    }

    /**
     * Reads and compiles a JCA file.
     *
     * @param path path to the JCA source file
     * @return compilation result
     * @throws IOException if the file cannot be read
     */
    public static CompilationResult compile(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return compile(JcaSource.from(path));
    }

    /**
     * Compiles an already constructed JCA source.
     *
     * @param source named JCA source
     * @return compilation result
     */
    public static CompilationResult compile(JcaSource source) {
        Objects.requireNonNull(source, "source");
        return new JcaCompiler().compile(source);
    }

    /**
     * Parses one command-line invocation against a compiled specification.
     *
     * @param spec compiled CLI specification
     * @param args command-line tokens
     * @return parse result containing either a parsed command line or error diagnostics
     */
    public static ParseResult parse(CliSpec spec, String... args) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(args, "args");
        return new CliParser().parse(spec, args);
    }

    /**
     * Renders concise help without an explicit executable name.
     *
     * @param spec compiled CLI specification
     * @return rendered help
     */
    public static String help(CliSpec spec) {
        Objects.requireNonNull(spec, "spec");
        return new HelpRenderer().render(spec);
    }

    /**
     * Renders concise help using the supplied executable or program name.
     *
     * @param spec compiled CLI specification
     * @param programName name shown in the usage line
     * @return rendered help
     */
    public static String help(CliSpec spec, String programName) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(programName, "programName");
        return new HelpRenderer().render(spec, programName);
    }

    /**
     * Renders the extended manual without an explicit executable name.
     *
     * @param spec compiled CLI specification
     * @return rendered manual
     */
    public static String manual(CliSpec spec) {
        Objects.requireNonNull(spec, "spec");
        return new ManualRenderer().render(spec);
    }

    /**
     * Renders the extended manual using the supplied executable or program name.
     *
     * @param spec compiled CLI specification
     * @param programName name shown in the usage line
     * @return rendered manual
     */
    public static String manual(CliSpec spec, String programName) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(programName, "programName");
        return new ManualRenderer().render(spec, programName);
    }
}
