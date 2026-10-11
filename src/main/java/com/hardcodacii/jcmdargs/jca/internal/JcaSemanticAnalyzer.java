package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.api.CompilationResult;
import com.hardcodacii.jcmdargs.diagnostic.Diagnostic;
import com.hardcodacii.jcmdargs.model.ArgumentCategory;
import com.hardcodacii.jcmdargs.model.ArgumentSpec;
import com.hardcodacii.jcmdargs.model.ArgumentValueSpec;
import com.hardcodacii.jcmdargs.model.CliSpec;
import com.hardcodacii.jcmdargs.model.CommandSpec;
import com.hardcodacii.jcmdargs.model.DefaultArgumentValue;
import com.hardcodacii.jcmdargs.model.Documentation;
import com.hardcodacii.jcmdargs.model.EnumOptionValue;
import com.hardcodacii.jcmdargs.model.FreeOptionValue;
import com.hardcodacii.jcmdargs.model.NoOptionValue;
import com.hardcodacii.jcmdargs.model.OptionSpec;
import com.hardcodacii.jcmdargs.model.OptionValueSpec;
import com.hardcodacii.jcmdargs.model.OptionalArgumentValue;
import com.hardcodacii.jcmdargs.model.OrderPolicy;
import com.hardcodacii.jcmdargs.model.RequiredArgumentValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public final class JcaSemanticAnalyzer {

    private static final Pattern LONG_ALIAS =
            Pattern.compile("--[A-Za-z0-9][A-Za-z0-9-]*");

    private static final Pattern SHORT_ALIAS =
            Pattern.compile("-[A-Za-z0-9][A-Za-z0-9-]*");

    private static final Pattern ARGUMENT_NAME =
            Pattern.compile("[A-Za-z][A-Za-z0-9_-]*");

    private static final Pattern COMMAND_NAME =
            Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]*");

    public CompilationResult analyze(
            JcaDocument document,
            List<Diagnostic> initialDiagnostics
    ) {
        Objects.requireNonNull(document, "document");
        Objects.requireNonNull(initialDiagnostics, "initialDiagnostics");

        List<Diagnostic> diagnostics = new ArrayList<>(initialDiagnostics);

        List<CommandDeclaration> commandDeclarations = statements(
                document,
                CommandDeclaration.class
        );
        List<OptionDeclaration> optionDeclarations = statements(
                document,
                OptionDeclaration.class
        );
        List<ArgumentDeclaration> argumentDeclarations = statements(
                document,
                ArgumentDeclaration.class
        );
        List<ArgumentsNumberDeclaration> countDeclarations = statements(
                document,
                ArgumentsNumberDeclaration.class
        );
        List<OrderDeclaration> orderDeclarations = statements(
                document,
                OrderDeclaration.class
        );
        List<DocumentationDeclaration> documentationDeclarations = statements(
                document,
                DocumentationDeclaration.class
        );

        validateCommandDeclarations(commandDeclarations, diagnostics);
        validateOptionDeclarations(optionDeclarations, diagnostics);
        validateArgumentDeclarations(argumentDeclarations, diagnostics);
        validateArgumentCount(
                countDeclarations,
                argumentDeclarations.size(),
                diagnostics
        );
        Optional<OrderPolicy> orderPolicy = validateOrderPolicy(
                orderDeclarations,
                !commandDeclarations.isEmpty(),
                !optionDeclarations.isEmpty(),
                !argumentDeclarations.isEmpty(),
                diagnostics
        );

        DocumentationIndex documentation = resolveDocumentation(
                documentationDeclarations,
                commandDeclarations,
                optionDeclarations,
                argumentDeclarations,
                diagnostics
        );

        if (diagnostics.stream().anyMatch(Diagnostic::isError)) {
            return CompilationResult.failure(diagnostics);
        }

        Optional<CommandSpec> command = commandDeclarations.stream()
                .findFirst()
                .map(declaration -> new CommandSpec(
                        declaration.name(),
                        documentation.commandDocumentation(declaration.name())
                ));

        List<OptionSpec> options = optionDeclarations.stream()
                .map(declaration -> toOptionSpec(declaration, documentation))
                .toList();

        List<ArgumentSpec> arguments = new ArrayList<>();
        for (int index = 0; index < argumentDeclarations.size(); index++) {
            ArgumentDeclaration declaration = argumentDeclarations.get(index);

            arguments.add(new ArgumentSpec(
                    declaration.name(),
                    index,
                    toArgumentValueSpec(declaration.value()),
                    documentation.argumentDocumentation(declaration.name())
            ));
        }

        CliSpec spec = new CliSpec(
                command,
                options,
                arguments,
                orderPolicy,
                documentation.rootDocumentation()
        );

        return CompilationResult.success(spec, diagnostics);
    }

    private static void validateCommandDeclarations(
            List<CommandDeclaration> declarations,
            List<Diagnostic> diagnostics
    ) {
        if (declarations.size() > 1) {
            for (int index = 1; index < declarations.size(); index++) {
                diagnostics.add(Diagnostic.error(
                        "JCA302",
                        "Only one command definition is supported in JCA 1.0.",
                        declarations.get(index).sourceLocation()
                ));
            }
        }

        for (CommandDeclaration declaration : declarations) {
            String name = declaration.name();

            if (name.isBlank()
                    || name.equals("#")
                    || name.startsWith("-")
                    || !COMMAND_NAME.matcher(name).matches()) {
                diagnostics.add(Diagnostic.error(
                        "JCA301",
                        "Invalid command: " + name,
                        declaration.sourceLocation()
                ));
            }
        }
    }

    private static void validateOptionDeclarations(
            List<OptionDeclaration> declarations,
            List<Diagnostic> diagnostics
    ) {
        Map<String, OptionDeclaration> aliases = new LinkedHashMap<>();

        for (OptionDeclaration declaration : declarations) {
            if (declaration.required() && declaration.standalone()) {
                diagnostics.add(Diagnostic.error(
                        "JCA107",
                        "An option cannot be both required and standalone.",
                        declaration.sourceLocation()
                ));
            }

            if (declaration.aliases().isEmpty()) {
                diagnostics.add(Diagnostic.error(
                        "JCA101",
                        "An option must declare at least one alias.",
                        declaration.sourceLocation()
                ));
                continue;
            }

            Set<String> localAliases = new HashSet<>();

            for (String alias : declaration.aliases()) {
                if (!localAliases.add(alias)) {
                    diagnostics.add(Diagnostic.error(
                            "JCA102",
                            "Duplicate alias in option declaration: " + alias,
                            declaration.sourceLocation()
                    ));
                }

                if (!isValidAlias(alias)) {
                    diagnostics.add(Diagnostic.error(
                            "JCA104",
                            "Invalid option alias: " + alias,
                            declaration.sourceLocation()
                    ));
                }

                OptionDeclaration previous = aliases.putIfAbsent(
                        alias,
                        declaration
                );

                if (previous != null && previous != declaration) {
                    diagnostics.add(Diagnostic.error(
                            "JCA103",
                            "Option alias is already used: " + alias,
                            declaration.sourceLocation()
                    ));
                }
            }

            validateOptionValue(declaration, diagnostics);
        }
    }

    private static void validateOptionValue(
            OptionDeclaration declaration,
            List<Diagnostic> diagnostics
    ) {
        if (declaration.value() instanceof OptionDeclaration.EnumValue enumValue) {
            List<String> values = enumValue.allowedValues();

            if (values.isEmpty()) {
                diagnostics.add(Diagnostic.error(
                        "JCA105",
                        "Enum option value set must not be empty.",
                        declaration.sourceLocation()
                ));
                return;
            }

            Set<String> unique = new HashSet<>();

            for (String value : values) {
                if (value.isBlank()) {
                    diagnostics.add(Diagnostic.error(
                            "JCA107",
                            "Option values must not be blank.",
                            declaration.sourceLocation()
                    ));
                }
                if (!unique.add(value)) {
                    diagnostics.add(Diagnostic.error(
                            "JCA106",
                            "Duplicate option value: " + value,
                            declaration.sourceLocation()
                    ));
                }
            }

            if (enumValue.implicitValue().isPresent()
                    && !values.contains(enumValue.implicitValue().get())) {
                diagnostics.add(Diagnostic.error(
                        "JCA107",
                        "Implicit option value must belong to the allowed values.",
                        declaration.sourceLocation()
                ));
            }
        }
    }

    private static void validateArgumentDeclarations(
            List<ArgumentDeclaration> declarations,
            List<Diagnostic> diagnostics
    ) {
        Set<String> names = new HashSet<>();

        boolean optionalSeen = false;

        for (ArgumentDeclaration declaration : declarations) {
            if (!ARGUMENT_NAME.matcher(declaration.name()).matches()) {
                diagnostics.add(Diagnostic.error(
                        "JCA202",
                        "Invalid argument name: " + declaration.name(),
                        declaration.sourceLocation()
                ));
            }

            if (!names.add(declaration.name())) {
                diagnostics.add(Diagnostic.error(
                        "JCA201",
                        "Duplicate argument name: " + declaration.name(),
                        declaration.sourceLocation()
                ));
            }

            boolean required =
                    declaration.value() instanceof ArgumentDeclaration.RequiredValue;

            if (optionalSeen && required) {
                diagnostics.add(Diagnostic.error(
                        "JCA202",
                        "A required positional argument cannot follow an optional/default argument.",
                        declaration.sourceLocation()
                ));
            }

            if (!required) {
                optionalSeen = true;
            }
        }
    }

    private static void validateArgumentCount(
            List<ArgumentsNumberDeclaration> declarations,
            int actualCount,
            List<Diagnostic> diagnostics
    ) {
        if (declarations.size() > 1) {
            for (int index = 1; index < declarations.size(); index++) {
                diagnostics.add(Diagnostic.error(
                        "JCA206",
                        "arguments_number may be declared only once.",
                        declarations.get(index).sourceLocation()
                ));
            }
        }

        if (declarations.isEmpty()) {
            return;
        }

        ArgumentsNumberDeclaration declaration = declarations.getFirst();
        int declaredCount;

        try {
            declaredCount = Integer.parseInt(declaration.rawValue());
        } catch (NumberFormatException exception) {
            diagnostics.add(Diagnostic.error(
                    "JCA205",
                    "arguments_number must be a non-negative integer.",
                    declaration.sourceLocation()
            ));
            return;
        }

        if (declaredCount < 0) {
            diagnostics.add(Diagnostic.error(
                    "JCA205",
                    "arguments_number must be a non-negative integer.",
                    declaration.sourceLocation()
            ));
            return;
        }

        if (declaredCount != actualCount) {
            diagnostics.add(Diagnostic.error(
                    "JCA203",
                    "arguments_number declares "
                            + declaredCount
                            + " positional arguments, but "
                            + actualCount
                            + " argument definitions were found.",
                    declaration.sourceLocation()
            ));
        }
    }

    private static Optional<OrderPolicy> validateOrderPolicy(
            List<OrderDeclaration> declarations,
            boolean hasCommand,
            boolean hasOptions,
            boolean hasArguments,
            List<Diagnostic> diagnostics
    ) {
        if (declarations.size() > 1) {
            for (int index = 1; index < declarations.size(); index++) {
                diagnostics.add(Diagnostic.error(
                        "JCA001",
                        "allowed_arguments_order may be declared only once.",
                        declarations.get(index).sourceLocation()
                ));
            }
        }

        if (declarations.isEmpty()) {
            return Optional.empty();
        }

        OrderDeclaration declaration = declarations.getFirst();
        List<ArgumentCategory> categories = new ArrayList<>();
        Set<ArgumentCategory> unique = new HashSet<>();

        for (String rawCategory : declaration.categories()) {
            ArgumentCategory category = switch (
                    rawCategory.toLowerCase(Locale.ROOT)
            ) {
                case "command" -> ArgumentCategory.COMMAND;
                case "option" -> ArgumentCategory.OPTION;
                case "argument" -> ArgumentCategory.ARGUMENT;
                default -> null;
            };

            if (category == null) {
                diagnostics.add(Diagnostic.error(
                        "JCA401",
                        "Unknown argument category: " + rawCategory,
                        declaration.sourceLocation()
                ));
                continue;
            }

            if (!unique.add(category)) {
                diagnostics.add(Diagnostic.error(
                        "JCA402",
                        "Duplicate argument category: " + rawCategory,
                        declaration.sourceLocation()
                ));
            } else {
                categories.add(category);
            }
        }

        if (hasCommand && !unique.contains(ArgumentCategory.COMMAND)) {
            diagnostics.add(Diagnostic.error(
                    "JCA403",
                    "Order policy does not include command.",
                    declaration.sourceLocation()
            ));
        }

        if (hasOptions && !unique.contains(ArgumentCategory.OPTION)) {
            diagnostics.add(Diagnostic.error(
                    "JCA403",
                    "Order policy does not include option.",
                    declaration.sourceLocation()
            ));
        }

        if (hasArguments && !unique.contains(ArgumentCategory.ARGUMENT)) {
            diagnostics.add(Diagnostic.error(
                    "JCA403",
                    "Order policy does not include argument.",
                    declaration.sourceLocation()
            ));
        }

        if (categories.isEmpty()) {
            return Optional.empty();
        }

        if (diagnostics.stream().anyMatch(Diagnostic::isError)) {
            return Optional.empty();
        }

        return Optional.of(new OrderPolicy(categories));
    }

    private static DocumentationIndex resolveDocumentation(
            List<DocumentationDeclaration> declarations,
            List<CommandDeclaration> commands,
            List<OptionDeclaration> options,
            List<ArgumentDeclaration> arguments,
            List<Diagnostic> diagnostics
    ) {
        Set<String> commandNames = new HashSet<>();
        commands.forEach(command -> commandNames.add(command.name()));

        Set<String> primaryAliases = new HashSet<>();
        for (OptionDeclaration option : options) {
            if (!option.aliases().isEmpty()) {
                primaryAliases.add(option.aliases().getFirst());
            }
        }

        Set<String> argumentNames = new HashSet<>();
        arguments.forEach(argument -> argumentNames.add(argument.name()));

        Map<DocumentationKey, String> values = new LinkedHashMap<>();

        for (DocumentationDeclaration declaration : declarations) {
            boolean targetExists = switch (declaration.targetType()) {
                case ROOT -> true;
                case COMMAND -> declaration.target()
                        .filter(commandNames::contains)
                        .isPresent();
                case OPTION -> declaration.target()
                        .filter(primaryAliases::contains)
                        .isPresent();
                case ARGUMENT -> declaration.target()
                        .filter(argumentNames::contains)
                        .isPresent();
            };

            if (!targetExists) {
                diagnostics.add(Diagnostic.error(
                        "JCA501",
                        "Documentation target does not exist: "
                                + declaration.target().orElse("<root>"),
                        declaration.sourceLocation()
                ));
                continue;
            }

            DocumentationKey key = new DocumentationKey(
                    declaration.targetType(),
                    declaration.target(),
                    declaration.field()
            );

            if (values.putIfAbsent(key, declaration.text()) != null) {
                diagnostics.add(Diagnostic.error(
                        "JCA502",
                        "Documentation field is declared more than once.",
                        declaration.sourceLocation()
                ));
            }
        }

        return new DocumentationIndex(values);
    }

    private static OptionSpec toOptionSpec(
            OptionDeclaration declaration,
            DocumentationIndex documentation
    ) {
        OptionValueSpec valueSpec = switch (declaration.value()) {
            case OptionDeclaration.NoValue ignored ->
                    new NoOptionValue();
            case OptionDeclaration.FreeValue freeValue ->
                    new FreeOptionValue(freeValue.implicitValue());
            case OptionDeclaration.EnumValue enumValue ->
                    new EnumOptionValue(
                            enumValue.allowedValues(),
                            enumValue.implicitValue()
                    );
        };

        String primaryAlias = declaration.aliases().getFirst();

        return new OptionSpec(
                primaryAlias,
                declaration.aliases(),
                declaration.required(),
                declaration.standalone(),
                valueSpec,
                documentation.optionDocumentation(primaryAlias)
        );
    }

    private static ArgumentValueSpec toArgumentValueSpec(
            ArgumentDeclaration.ValueDeclaration value
    ) {
        return switch (value) {
            case ArgumentDeclaration.RequiredValue ignored ->
                    new RequiredArgumentValue();
            case ArgumentDeclaration.OptionalValue ignored ->
                    new OptionalArgumentValue();
            case ArgumentDeclaration.DefaultValue defaultValue ->
                    new DefaultArgumentValue(defaultValue.value());
        };
    }

    private static boolean isValidAlias(String alias) {
        return LONG_ALIAS.matcher(alias).matches()
                || SHORT_ALIAS.matcher(alias).matches();
    }

    private static <T extends JcaStatement> List<T> statements(
            JcaDocument document,
            Class<T> type
    ) {
        return document.statements().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    private record DocumentationKey(
            DocumentationDeclaration.TargetType targetType,
            Optional<String> target,
            DocumentationDeclaration.Field field
    ) {
    }

    private record DocumentationIndex(
            Map<DocumentationKey, String> values
    ) {
        DocumentationIndex {
            values = Map.copyOf(values);
        }

        Documentation rootDocumentation() {
            return documentationFor(
                    DocumentationDeclaration.TargetType.ROOT,
                    Optional.empty()
            );
        }

        Documentation commandDocumentation(String command) {
            return documentationFor(
                    DocumentationDeclaration.TargetType.COMMAND,
                    Optional.of(command)
            );
        }

        Documentation optionDocumentation(String primaryAlias) {
            return documentationFor(
                    DocumentationDeclaration.TargetType.OPTION,
                    Optional.of(primaryAlias)
            );
        }

        Documentation argumentDocumentation(String argument) {
            return documentationFor(
                    DocumentationDeclaration.TargetType.ARGUMENT,
                    Optional.of(argument)
            );
        }

        private Documentation documentationFor(
                DocumentationDeclaration.TargetType targetType,
                Optional<String> target
        ) {
            Optional<String> summary = Optional.ofNullable(values.get(
                    new DocumentationKey(
                            targetType,
                            target,
                            DocumentationDeclaration.Field.SUMMARY
                    )
            ));

            Optional<String> description = Optional.ofNullable(values.get(
                    new DocumentationKey(
                            targetType,
                            target,
                            DocumentationDeclaration.Field.DESCRIPTION
                    )
            ));

            return new Documentation(summary, description);
        }
    }
}
