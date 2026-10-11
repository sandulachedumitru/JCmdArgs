package com.hardcodacii.jcmdargs.runtime;

import com.hardcodacii.jcmdargs.api.ParseResult;
import com.hardcodacii.jcmdargs.diagnostic.Diagnostic;
import com.hardcodacii.jcmdargs.model.ArgumentCategory;
import com.hardcodacii.jcmdargs.model.ArgumentSpec;
import com.hardcodacii.jcmdargs.model.CliSpec;
import com.hardcodacii.jcmdargs.model.DefaultArgumentValue;
import com.hardcodacii.jcmdargs.model.EnumOptionValue;
import com.hardcodacii.jcmdargs.model.FreeOptionValue;
import com.hardcodacii.jcmdargs.model.NoOptionValue;
import com.hardcodacii.jcmdargs.model.OptionSpec;
import com.hardcodacii.jcmdargs.model.OptionalArgumentValue;
import com.hardcodacii.jcmdargs.model.RequiredArgumentValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Parses one command-line invocation against an immutable CLI specification.
 */
public final class CliParser {

    /**
     * Parses a single invocation. All mutable parsing state is local to this call.
     *
     * @param spec compiled CLI specification
     * @param args command-line tokens
     * @return parse result
     */
    public ParseResult parse(CliSpec spec, String... args) {
        Objects.requireNonNull(spec, "spec");
        Objects.requireNonNull(args, "args");

        String[] tokens = args.clone();
        for (String token : tokens) {
            Objects.requireNonNull(token, "args must not contain null tokens");
        }

        State state = new State(spec);

        for (int index = 0; index < tokens.length; index++) {
            String token = tokens[index];

            if (!state.endOfOptions && token.equals("--")) {
                state.endOfOptions = true;
                continue;
            }

            if (!state.endOfOptions) {
                OptionToken optionToken = resolveOptionToken(spec, token);

                if (optionToken != null) {
                    state.checkOrder(ArgumentCategory.OPTION, index);
                    int consumed = parseOption(spec, state, optionToken, tokens, index);
                    index += consumed;
                    continue;
                }

                if (token.startsWith("-")) {
                    state.error(
                            "CLI001",
                            "Unknown option '" + token + "' at token " + index + "."
                    );
                    continue;
                }

                if (state.command.isEmpty()
                        && spec.command().isPresent()
                        && token.equals(spec.command().orElseThrow().name())) {
                    state.checkOrder(ArgumentCategory.COMMAND, index);
                    state.command = Optional.of(token);
                    continue;
                }
            }

            state.checkOrder(ArgumentCategory.ARGUMENT, index);
            state.addPositional(token, index);
        }

        state.finish();

        if (state.hasErrors()) {
            return ParseResult.failure(state.diagnostics);
        }

        return ParseResult.success(
                new ParsedCommandLine(
                        spec,
                        state.command,
                        state.parsedOptions,
                        state.arguments
                ),
                state.diagnostics
        );
    }

    private static int parseOption(
            CliSpec spec,
            State state,
            OptionToken token,
            String[] tokens,
            int index
    ) {
        OptionSpec option = token.spec();
        Optional<String> value = Optional.empty();
        int consumed = 0;

        if (option.valueSpec() instanceof NoOptionValue) {
            if (token.explicitValue().isPresent()) {
                state.error(
                        "CLI002",
                        "Option '" + token.matchedAlias() + "' does not accept a value."
                );
            }
        } else if (option.valueSpec() instanceof EnumOptionValue enumValue) {
            Optional<String> resolved = token.explicitValue();

            if (resolved.isEmpty()) {
                if (enumValue.implicitValue().isPresent()) {
                    resolved = enumValue.implicitValue();
                } else {
                    ValueConsumption consumption = consumeRequiredValue(
                            spec,
                            tokens,
                            index,
                            token.matchedAlias()
                    );

                    if (consumption.value().isPresent()) {
                        resolved = consumption.value();
                        consumed = consumption.consumedTokens();
                    } else {
                        state.error(
                                "CLI004",
                                "Option '" + token.matchedAlias() + "' requires a value."
                        );
                    }
                }
            }

            if (resolved.isPresent()) {
                if (!enumValue.allowedValues().contains(resolved.get())) {
                    state.error(
                            "CLI003",
                            "Invalid value '" + resolved.get() + "' for option '"
                                    + token.matchedAlias() + "'. Allowed values: "
                                    + String.join(", ", enumValue.allowedValues()) + "."
                    );
                } else {
                    value = resolved;
                }
            }
        } else if (option.valueSpec() instanceof FreeOptionValue freeValue) {
            Optional<String> resolved = token.explicitValue();

            if (resolved.isEmpty()) {
                if (freeValue.implicitValue().isPresent()) {
                    resolved = freeValue.implicitValue();
                } else {
                    ValueConsumption consumption = consumeRequiredValue(
                            spec,
                            tokens,
                            index,
                            token.matchedAlias()
                    );

                    if (consumption.value().isPresent()) {
                        resolved = consumption.value();
                        consumed = consumption.consumedTokens();
                    } else {
                        state.error(
                                "CLI004",
                                "Option '" + token.matchedAlias() + "' requires a value."
                        );
                    }
                }
            }

            value = resolved;
        }

        if (!state.seenPrimaryAliases.add(option.primaryAlias())) {
            state.error(
                    "CLI401",
                    "Option '" + option.primaryAlias() + "' was specified more than once."
            );
            return consumed;
        }

        state.parsedOptions.add(new ParsedOption(
                option.primaryAlias(),
                token.matchedAlias(),
                value
        ));

        if (option.standalone()) {
            state.standalonePrimaryAliases.add(option.primaryAlias());
        }

        return consumed;
    }

    private static ValueConsumption consumeRequiredValue(
            CliSpec spec,
            String[] tokens,
            int optionIndex,
            String matchedAlias
    ) {
        int valueIndex = optionIndex + 1;

        if (valueIndex >= tokens.length) {
            return ValueConsumption.missing();
        }

        String next = tokens[valueIndex];

        if (next.equals("--")) {
            return ValueConsumption.missing();
        }

        if (resolveOptionToken(spec, next) != null) {
            return ValueConsumption.missing();
        }

        return new ValueConsumption(Optional.of(next), 1);
    }

    private static OptionToken resolveOptionToken(CliSpec spec, String token) {
        Optional<OptionSpec> exact = spec.option(token);

        if (exact.isPresent()) {
            return new OptionToken(exact.get(), token, Optional.empty());
        }

        int separator = token.indexOf('=');
        if (separator <= 0) {
            return null;
        }

        String alias = token.substring(0, separator);
        Optional<OptionSpec> option = spec.option(alias);

        if (option.isEmpty()) {
            return null;
        }

        return new OptionToken(
                option.get(),
                alias,
                Optional.of(token.substring(separator + 1))
        );
    }

    private record OptionToken(
            OptionSpec spec,
            String matchedAlias,
            Optional<String> explicitValue
    ) {
    }

    private record ValueConsumption(
            Optional<String> value,
            int consumedTokens
    ) {
        static ValueConsumption missing() {
            return new ValueConsumption(Optional.empty(), 0);
        }
    }

    private static final class State {
        private final CliSpec spec;
        private final List<Diagnostic> diagnostics = new ArrayList<>();
        private final List<ParsedOption> parsedOptions = new ArrayList<>();
        private final Set<String> seenPrimaryAliases = new LinkedHashSet<>();
        private final Set<String> standalonePrimaryAliases = new LinkedHashSet<>();
        private final List<String> positionalValues = new ArrayList<>();
        private final Map<String, String> arguments = new LinkedHashMap<>();

        private Optional<String> command = Optional.empty();
        private boolean endOfOptions;
        private int highestOrderIndex = -1;

        private State(CliSpec spec) {
            this.spec = spec;
        }

        private void addPositional(String value, int tokenIndex) {
            if (positionalValues.size() >= spec.arguments().size()) {
                error(
                        "CLI102",
                        "Too many positional arguments; unexpected value '"
                                + value + "' at token " + tokenIndex + "."
                );
                return;
            }

            positionalValues.add(value);
        }

        private void checkOrder(ArgumentCategory category, int tokenIndex) {
            if (spec.orderPolicy().isEmpty()) {
                return;
            }

            List<ArgumentCategory> categories = spec.orderPolicy()
                    .orElseThrow()
                    .categories();
            int current = categories.indexOf(category);

            if (current < highestOrderIndex) {
                error(
                        "CLI301",
                        "Argument category " + category.name().toLowerCase()
                                + " violates allowed_arguments_order at token "
                                + tokenIndex + "."
                );
                return;
            }

            highestOrderIndex = Math.max(highestOrderIndex, current);
        }

        private void finish() {
            boolean standalone = !standalonePrimaryAliases.isEmpty();

            if (standalone) {
                if (parsedOptions.size() != 1 || !positionalValues.isEmpty()) {
                    error(
                            "CLI201",
                            "A standalone option cannot be combined with other options or positional arguments."
                    );
                }

                return;
            }

            if (spec.command().isPresent() && command.isEmpty()) {
                error(
                        "CLI103",
                        "Required command '" + spec.command().orElseThrow().name() + "' is missing."
                );
            }

            for (OptionSpec option : spec.options()) {
                if (option.required()
                        && !seenPrimaryAliases.contains(option.primaryAlias())) {
                    error(
                            "CLI005",
                            "Required option '" + option.primaryAlias() + "' is missing."
                    );
                }
            }

            for (int index = 0; index < spec.arguments().size(); index++) {
                ArgumentSpec argument = spec.arguments().get(index);

                if (index < positionalValues.size()) {
                    arguments.put(argument.name(), positionalValues.get(index));
                    continue;
                }

                if (argument.valueSpec() instanceof RequiredArgumentValue) {
                    error(
                            "CLI101",
                            "Required positional argument '" + argument.name() + "' is missing."
                    );
                } else if (argument.valueSpec() instanceof DefaultArgumentValue defaultValue) {
                    arguments.put(argument.name(), defaultValue.defaultValue());
                } else if (!(argument.valueSpec() instanceof OptionalArgumentValue)) {
                    throw new IllegalStateException(
                            "Unknown argument value specification: " + argument.valueSpec()
                    );
                }
            }
        }

        private void error(String code, String message) {
            diagnostics.add(Diagnostic.error(code, message));
        }

        private boolean hasErrors() {
            return diagnostics.stream().anyMatch(Diagnostic::isError);
        }
    }
}
