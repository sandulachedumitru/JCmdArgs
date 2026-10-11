package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.model.CliSpec;
import com.hardcodacii.jcmdargs.model.OptionSpec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ParsedCommandLine {

    private final Optional<String> command;
    private final List<ParsedOption> options;
    private final Map<String, ParsedOption> optionsByAlias;
    private final Map<String, String> arguments;

    ParsedCommandLine(
            CliSpec spec,
            Optional<String> command,
            List<ParsedOption> options,
            Map<String, String> arguments
    ) {
        Objects.requireNonNull(spec, "spec");
        this.command = Objects.requireNonNull(command, "command");
        this.options = List.copyOf(Objects.requireNonNull(options, "options"));
        this.arguments = Map.copyOf(Objects.requireNonNull(arguments, "arguments"));
        this.optionsByAlias = buildAliasIndex(spec, this.options);
    }

    public Optional<String> command() {
        return command;
    }

    public List<ParsedOption> options() {
        return options;
    }

    public boolean hasOption(String alias) {
        Objects.requireNonNull(alias, "alias");
        return optionsByAlias.containsKey(alias);
    }

    public Optional<ParsedOption> option(String alias) {
        Objects.requireNonNull(alias, "alias");
        return Optional.ofNullable(optionsByAlias.get(alias));
    }

    public Optional<String> optionValue(String alias) {
        return option(alias).flatMap(ParsedOption::value);
    }

    public Map<String, String> arguments() {
        return arguments;
    }

    public Optional<String> argument(String name) {
        Objects.requireNonNull(name, "name");
        return Optional.ofNullable(arguments.get(name));
    }

    private static Map<String, ParsedOption> buildAliasIndex(
            CliSpec spec,
            List<ParsedOption> parsedOptions
    ) {
        Map<String, ParsedOption> byPrimaryAlias = new LinkedHashMap<>();

        for (ParsedOption parsedOption : parsedOptions) {
            byPrimaryAlias.put(parsedOption.primaryAlias(), parsedOption);
        }

        Map<String, ParsedOption> byAlias = new LinkedHashMap<>();

        for (OptionSpec optionSpec : spec.options()) {
            ParsedOption parsedOption = byPrimaryAlias.get(optionSpec.primaryAlias());

            if (parsedOption == null) {
                continue;
            }

            for (String alias : optionSpec.aliases()) {
                byAlias.put(alias, parsedOption);
            }
        }

        return Map.copyOf(byAlias);
    }
}
