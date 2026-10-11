package com.hardcodacii.jcmdargs.render.internal;

import com.hardcodacii.jcmdargs.model.ArgumentSpec;
import com.hardcodacii.jcmdargs.model.DefaultArgumentValue;
import com.hardcodacii.jcmdargs.model.EnumOptionValue;
import com.hardcodacii.jcmdargs.model.FreeOptionValue;
import com.hardcodacii.jcmdargs.model.NoOptionValue;
import com.hardcodacii.jcmdargs.model.OptionSpec;
import com.hardcodacii.jcmdargs.model.OptionalArgumentValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class RenderSupport {

    private RenderSupport() {
    }

    public static String optionSignature(OptionSpec option) {
        List<String> aliases = new ArrayList<>(option.aliases());
        aliases.sort(
                Comparator
                        .comparingInt(RenderSupport::aliasGroup)
                        .thenComparingInt(String::length)
                        .thenComparing(String::compareTo)
        );

        String aliasesText = String.join(", ", aliases);
        String valueSuffix = switch (option.valueSpec()) {
            case NoOptionValue ignored -> "";
            case EnumOptionValue enumValue ->
                    enumValue.implicitValue().isPresent()
                            ? " [<value>]"
                            : " <value>";
            case FreeOptionValue freeValue ->
                    freeValue.implicitValue().isPresent()
                            ? " [<value>]"
                            : " <value>";
        };

        return aliasesText + valueSuffix;
    }

    public static String optionMetadata(OptionSpec option) {
        List<String> parts = new ArrayList<>();

        if (option.required()) {
            parts.add("Required");
        }
        if (option.standalone()) {
            parts.add("Standalone");
        }

        switch (option.valueSpec()) {
            case NoOptionValue ignored -> {
            }
            case EnumOptionValue enumValue -> {
                parts.add("Values: " + String.join(", ", enumValue.allowedValues()));
                enumValue.implicitValue()
                        .ifPresent(value -> parts.add("Implicit: " + value));
            }
            case FreeOptionValue freeValue ->
                    freeValue.implicitValue()
                            .ifPresent(value -> parts.add("Implicit: " + value));
        }

        return terminateMetadata(parts);
    }

    public static String argumentSignature(ArgumentSpec argument) {
        return switch (argument.valueSpec()) {
            case com.hardcodacii.jcmdargs.model.RequiredArgumentValue ignored ->
                    "<" + argument.name() + ">";
            case OptionalArgumentValue ignored ->
                    "[" + argument.name() + "]";
            case DefaultArgumentValue ignored ->
                    "[" + argument.name() + "]";
        };
    }

    public static String argumentMetadata(ArgumentSpec argument) {
        List<String> parts = new ArrayList<>();

        switch (argument.valueSpec()) {
            case com.hardcodacii.jcmdargs.model.RequiredArgumentValue ignored ->
                    parts.add("Required");
            case OptionalArgumentValue ignored -> {
            }
            case DefaultArgumentValue defaultValue ->
                    parts.add("Default: " + defaultValue.defaultValue());
        }

        return terminateMetadata(parts);
    }

    public static String combine(String prose, String metadata) {
        if (prose == null || prose.isBlank()) {
            return metadata;
        }
        if (metadata == null || metadata.isBlank()) {
            return prose;
        }

        String normalizedProse = prose.trim();

        if (normalizedProse.endsWith(".")
                || normalizedProse.endsWith("!")
                || normalizedProse.endsWith("?")) {
            return normalizedProse + " " + metadata;
        }

        return normalizedProse + ". " + metadata;
    }

    public static String indentMultiline(String text, String indentation) {
        return text.lines()
                .map(line -> indentation + line)
                .reduce((left, right) -> left + "\n" + right)
                .orElse("");
    }

    private static int aliasGroup(String alias) {
        if (alias.startsWith("-") && !alias.startsWith("--")) {
            return 0;
        }
        if (alias.startsWith("--")) {
            return 1;
        }
        return 2;
    }

    private static String terminateMetadata(List<String> parts) {
        if (parts.isEmpty()) {
            return "";
        }

        return String.join(". ", parts) + ".";
    }
}
