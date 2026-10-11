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

/**
 * Renders concise terminal help from an immutable CLI specification.
 */
public final class HelpRenderer {

    private static final int GAP = 4;

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
                .filter(summary -> !summary.isBlank())
                .ifPresent(sections::add);

        sections.add(
                programName
                        .map(name -> usageRenderer.render(spec, name))
                        .orElseGet(() -> usageRenderer.render(spec))
        );

        if (spec.command().isPresent()) {
            var command = spec.command().orElseThrow();
            String summary = command.documentation()
                    .summary()
                    .filter(text -> !text.isBlank())
                    .orElse("");

            if (!summary.isBlank()) {
                sections.add(
                        "Command:\n"
                                + formatRows(List.of(
                                        new Row(command.name(), summary)
                                ))
                );
            }
        }

        if (!spec.options().isEmpty()) {
            List<Row> rows = spec.options().stream()
                    .map(this::optionRow)
                    .toList();

            sections.add("Options:\n" + formatRows(rows));
        }

        if (!spec.arguments().isEmpty()) {
            List<Row> rows = spec.arguments().stream()
                    .map(this::argumentRow)
                    .toList();

            sections.add("Arguments:\n" + formatRows(rows));
        }

        return String.join("\n\n", sections);
    }

    private Row optionRow(OptionSpec option) {
        String prose = option.documentation().summary().orElse("");
        String metadata = optionMetadata(option);

        return new Row(
                optionSignature(option),
                combine(prose, metadata)
        );
    }

    private Row argumentRow(ArgumentSpec argument) {
        String prose = argument.documentation().summary().orElse("");
        String metadata = argumentMetadata(argument);

        return new Row(
                argument.name(),
                combine(prose, metadata)
        );
    }

    private static String formatRows(List<Row> rows) {
        int width = rows.stream()
                .map(Row::left)
                .mapToInt(String::length)
                .max()
                .orElse(0);

        return rows.stream()
                .map(row -> {
                    String prefix = "  " + row.left();

                    if (row.right().isBlank()) {
                        return prefix;
                    }

                    return prefix
                            + " ".repeat(width - row.left().length() + GAP)
                            + row.right();
                })
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
    }

    private static String optionSignature(OptionSpec option) {
        return RenderSupport.optionSignature(option);
    }

    private static String optionMetadata(OptionSpec option) {
        return RenderSupport.optionMetadata(option);
    }

    private static String argumentMetadata(ArgumentSpec argument) {
        return RenderSupport.argumentMetadata(argument);
    }

    private static String combine(String prose, String metadata) {
        return RenderSupport.combine(prose, metadata);
    }

    private record Row(String left, String right) {
    }
}
