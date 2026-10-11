package com.hardcodacii.jcmdargs.render;

import com.hardcodacii.jcmdargs.model.ArgumentSpec;
import com.hardcodacii.jcmdargs.model.CliSpec;
import com.hardcodacii.jcmdargs.model.OptionSpec;
import com.hardcodacii.jcmdargs.render.internal.RenderSupport;
import com.hardcodacii.jcmdargs.render.internal.UsageRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ManualRenderer {

    private final UsageRenderer usageRenderer = new UsageRenderer();

    public String render(CliSpec spec) {
        Objects.requireNonNull(spec, "spec");
        return render(spec, Optional.empty());
    }

    public String render(CliSpec spec, String programName) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(programName, "programName");

        if (programName.isBlank()) {
            throw new IllegalArgumentException("programName must not be blank");
        }

        return render(spec, Optional.of(programName));
    }

    private String render(CliSpec spec, Optional<String> programName) {
        List<String> sections = new ArrayList<>();

        spec.documentation().summary()
                .filter(text -> !text.isBlank())
                .ifPresent(sections::add);

        spec.documentation().description()
                .filter(text -> !text.isBlank())
                .ifPresent(sections::add);

        sections.add(
                programName
                        .map(name -> usageRenderer.render(spec, name))
                        .orElseGet(() -> usageRenderer.render(spec))
        );

        spec.command().ifPresent(command -> {
            List<String> body = new ArrayList<>();
            body.add(command.name());

            command.documentation().summary()
                    .filter(text -> !text.isBlank())
                    .ifPresent(body::add);

            command.documentation().description()
                    .filter(text -> !text.isBlank())
                    .ifPresent(body::add);

            sections.add("Command:\n" + indentBody(body));
        });

        if (!spec.options().isEmpty()) {
            List<String> options = spec.options().stream()
                    .map(this::renderOption)
                    .toList();

            sections.add("Options:\n" + String.join("\n\n", options));
        }

        if (!spec.arguments().isEmpty()) {
            List<String> arguments = spec.arguments().stream()
                    .map(this::renderArgument)
                    .toList();

            sections.add("Arguments:\n" + String.join("\n\n", arguments));
        }

        return String.join("\n\n", sections);
    }

    private String renderOption(OptionSpec option) {
        List<String> lines = new ArrayList<>();
        lines.add("  " + RenderSupport.optionSignature(option));

        option.documentation().summary()
                .filter(text -> !text.isBlank())
                .ifPresent(text -> lines.add(
                        RenderSupport.indentMultiline(text, "    ")
                ));

        option.documentation().description()
                .filter(text -> !text.isBlank())
                .ifPresent(text -> lines.add(
                        RenderSupport.indentMultiline(text, "    ")
                ));

        String metadata = RenderSupport.optionMetadata(option);
        if (!metadata.isBlank()) {
            lines.add("    " + metadata);
        }

        return String.join("\n", lines);
    }

    private String renderArgument(ArgumentSpec argument) {
        List<String> lines = new ArrayList<>();
        lines.add("  " + RenderSupport.argumentSignature(argument));

        argument.documentation().summary()
                .filter(text -> !text.isBlank())
                .ifPresent(text -> lines.add(
                        RenderSupport.indentMultiline(text, "    ")
                ));

        argument.documentation().description()
                .filter(text -> !text.isBlank())
                .ifPresent(text -> lines.add(
                        RenderSupport.indentMultiline(text, "    ")
                ));

        String metadata = RenderSupport.argumentMetadata(argument);
        if (!metadata.isBlank()) {
            lines.add("    " + metadata);
        }

        return String.join("\n", lines);
    }

    private static String indentBody(List<String> lines) {
        return lines.stream()
                .map(line -> RenderSupport.indentMultiline(line, "  "))
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
    }
}
