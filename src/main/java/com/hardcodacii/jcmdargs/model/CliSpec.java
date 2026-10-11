package com.hardcodacii.jcmdargs.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class CliSpec {

    private final Optional<CommandSpec> command;
    private final List<OptionSpec> options;
    private final List<ArgumentSpec> arguments;
    private final Optional<OrderPolicy> orderPolicy;
    private final Documentation documentation;

    private final Map<String, OptionSpec> optionsByAlias;
    private final Map<String, ArgumentSpec> argumentsByName;

    public CliSpec(
            Optional<CommandSpec> command,
            List<OptionSpec> options,
            List<ArgumentSpec> arguments,
            Optional<OrderPolicy> orderPolicy,
            Documentation documentation
    ) {
        this.command = Objects.requireNonNull(command, "command");
        this.options = List.copyOf(Objects.requireNonNull(options, "options"));
        this.arguments = List.copyOf(Objects.requireNonNull(arguments, "arguments"));
        this.orderPolicy = Objects.requireNonNull(orderPolicy, "orderPolicy");
        this.documentation = Objects.requireNonNull(documentation, "documentation");

        this.optionsByAlias = buildOptionIndex(this.options);
        this.argumentsByName = buildArgumentIndex(this.arguments);
        validateArgumentPositions(this.arguments);
    }

    public Optional<CommandSpec> command() {
        return command;
    }

    public List<OptionSpec> options() {
        return options;
    }

    public List<ArgumentSpec> arguments() {
        return arguments;
    }

    public Optional<OrderPolicy> orderPolicy() {
        return orderPolicy;
    }

    public Documentation documentation() {
        return documentation;
    }

    public Optional<OptionSpec> option(String alias) {
        Objects.requireNonNull(alias, "alias");
        return Optional.ofNullable(optionsByAlias.get(alias));
    }

    public Optional<ArgumentSpec> argument(String name) {
        Objects.requireNonNull(name, "name");
        return Optional.ofNullable(argumentsByName.get(name));
    }

    private static Map<String, OptionSpec> buildOptionIndex(List<OptionSpec> options) {
        Map<String, OptionSpec> index = new LinkedHashMap<>();

        for (OptionSpec option : options) {
            for (String alias : option.aliases()) {
                OptionSpec existing = index.putIfAbsent(alias, option);

                if (existing != null) {
                    throw new IllegalArgumentException(
                            "duplicate option alias: " + alias
                    );
                }
            }
        }

        return Map.copyOf(index);
    }

    private static Map<String, ArgumentSpec> buildArgumentIndex(
            List<ArgumentSpec> arguments
    ) {
        Map<String, ArgumentSpec> index = new HashMap<>();

        for (ArgumentSpec argument : arguments) {
            ArgumentSpec existing = index.putIfAbsent(argument.name(), argument);

            if (existing != null) {
                throw new IllegalArgumentException(
                        "duplicate argument name: " + argument.name()
                );
            }
        }

        return Map.copyOf(index);
    }

    private static void validateArgumentPositions(List<ArgumentSpec> arguments) {
        var positions = new HashSet<Integer>();

        for (int index = 0; index < arguments.size(); index++) {
            ArgumentSpec argument = arguments.get(index);

            if (!positions.add(argument.position())) {
                throw new IllegalArgumentException(
                        "duplicate argument position: " + argument.position()
                );
            }
            if (argument.position() != index) {
                throw new IllegalArgumentException(
                        "argument positions must be contiguous and follow declaration order"
                );
            }
        }
    }
}
