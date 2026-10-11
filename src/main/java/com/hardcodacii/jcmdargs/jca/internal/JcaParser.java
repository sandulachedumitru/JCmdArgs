package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.Diagnostic;
import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;
import com.hardcodacii.jcmdargs.jca.JcaSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class JcaParser {

    public ParseResult parse(JcaSource source) {
        Objects.requireNonNull(source, "source");

        String normalized = source.content()
                .replace("\r\n", "\n")
                .replace('\r', '\n');

        String[] lines = normalized.split("\n", -1);
        List<JcaStatement> statements = new ArrayList<>();
        List<Diagnostic> diagnostics = new ArrayList<>();

        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            SourceLocation location = new SourceLocation(
                    source.name(),
                    index + 1,
                    firstNonWhitespaceColumn(line)
            );

            if (trimmed.startsWith("doc.")) {
                DocumentationParse documentationParse =
                        parseDocumentation(lines, index, source.name(), diagnostics);

                if (documentationParse.declaration() != null) {
                    statements.add(documentationParse.declaration());
                }

                index = documentationParse.lastLineIndex();
                continue;
            }

            if (trimmed.startsWith("command=")) {
                statements.add(new CommandDeclaration(
                        trimmed.substring("command=".length()).trim(),
                        location
                ));
                continue;
            }

            if (trimmed.startsWith("arguments_number=")) {
                statements.add(new ArgumentsNumberDeclaration(
                        trimmed.substring("arguments_number=".length()).trim(),
                        location
                ));
                continue;
            }

            if (trimmed.startsWith("allowed_arguments_order=")) {
                String body = trimmed.substring(
                        "allowed_arguments_order=".length()
                ).trim();

                List<String> categories = parseSimpleBracedList(
                        body,
                        location,
                        diagnostics
                );

                if (categories != null) {
                    statements.add(new OrderDeclaration(categories, location));
                }
                continue;
            }

            if (trimmed.startsWith("option=")) {
                OptionDeclaration declaration = parseOption(
                        trimmed.substring("option=".length()).trim(),
                        location,
                        diagnostics
                );

                if (declaration != null) {
                    statements.add(declaration);
                }
                continue;
            }

            if (trimmed.startsWith("argument=")) {
                ArgumentDeclaration declaration = parseArgument(
                        trimmed.substring("argument=".length()),
                        location,
                        diagnostics
                );

                if (declaration != null) {
                    statements.add(declaration);
                }
                continue;
            }

            diagnostics.add(Diagnostic.error(
                    "JCA002",
                    "Unknown JCA definition: " + trimmed,
                    location
            ));
        }

        return new ParseResult(
                new JcaDocument(statements),
                diagnostics
        );
    }

    private static OptionDeclaration parseOption(
            String body,
            SourceLocation location,
            List<Diagnostic> diagnostics
    ) {
        boolean required = false;
        boolean standalone = false;

        if (body.startsWith("+")) {
            required = true;
            body = body.substring(1).trim();
        } else if (body.startsWith("!")) {
            standalone = true;
            body = body.substring(1).trim();
        }

        if (body.startsWith("+") || body.startsWith("!")) {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Only one option modifier is allowed.",
                    location
            ));
            return null;
        }

        if (!body.startsWith("{")) {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Option aliases must be declared inside braces.",
                    location
            ));
            return null;
        }

        int aliasesEnd = findClosingBrace(body, 0);
        if (aliasesEnd < 0) {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Unterminated option alias list.",
                    location
            ));
            return null;
        }

        String aliasesBody = body.substring(1, aliasesEnd);
        List<ParsedValue> parsedAliases = parseCsv(
                aliasesBody,
                location,
                diagnostics
        );

        if (parsedAliases == null) {
            return null;
        }

        List<String> aliases = parsedAliases.stream()
                .map(ParsedValue::text)
                .toList();

        String tail = body.substring(aliasesEnd + 1).trim();

        OptionDeclaration.ValueDeclaration value;

        if (tail.isEmpty()) {
            value = new OptionDeclaration.NoValue();
        } else if (tail.equals("=#")) {
            value = new OptionDeclaration.FreeValue(Optional.empty());
        } else if (tail.startsWith("=#{") && tail.endsWith("}")) {
            String valuesBody = tail.substring(3, tail.length() - 1);
            List<ParsedValue> values = parseCsv(
                    valuesBody,
                    location,
                    diagnostics
            );

            if (values == null) {
                return null;
            }

            value = new OptionDeclaration.EnumValue(
                    values.stream().map(ParsedValue::text).toList(),
                    Optional.empty()
            );
        } else if (tail.startsWith("={") && tail.endsWith("}")) {
            String valuesBody = tail.substring(2, tail.length() - 1);
            List<ParsedValue> values = parseCsv(
                    valuesBody,
                    location,
                    diagnostics
            );

            if (values == null) {
                return null;
            }

            List<Integer> markerIndexes = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                ParsedValue item = values.get(i);
                if (!item.quoted() && item.text().equals("#")) {
                    markerIndexes.add(i);
                }
            }

            if (markerIndexes.isEmpty()) {
                value = new OptionDeclaration.EnumValue(
                        values.stream().map(ParsedValue::text).toList(),
                        values.isEmpty()
                                ? Optional.empty()
                                : Optional.of(values.getFirst().text())
                );
            } else if (markerIndexes.size() == 1
                    && markerIndexes.getFirst() == values.size() - 1
                    && values.size() == 2) {
                value = new OptionDeclaration.FreeValue(
                        Optional.of(values.getFirst().text())
                );
            } else {
                diagnostics.add(Diagnostic.error(
                        "JCA107",
                        "Invalid option value specification.",
                        location
                ));
                return null;
            }
        } else {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Invalid option declaration.",
                    location
            ));
            return null;
        }

        return new OptionDeclaration(
                required,
                standalone,
                aliases,
                value,
                location
        );
    }

    private static ArgumentDeclaration parseArgument(
            String body,
            SourceLocation location,
            List<Diagnostic> diagnostics
    ) {
        int separator = body.indexOf('=');

        if (separator < 0) {
            diagnostics.add(Diagnostic.error(
                    "JCA204",
                    "Legacy unnamed argument syntax is not valid in JCA 1.0.",
                    location
            ));
            return null;
        }

        String name = body.substring(0, separator).trim();
        String rawValue = body.substring(separator + 1).trim();

        if (rawValue.equals("#")) {
            return new ArgumentDeclaration(
                    name,
                    new ArgumentDeclaration.RequiredValue(),
                    location
            );
        }

        if (rawValue.equals("?")) {
            return new ArgumentDeclaration(
                    name,
                    new ArgumentDeclaration.OptionalValue(),
                    location
            );
        }

        ParsedValue value = parseSingleValue(
                rawValue,
                location,
                diagnostics
        );

        if (value == null) {
            return null;
        }

        return new ArgumentDeclaration(
                name,
                new ArgumentDeclaration.DefaultValue(value.text()),
                location
        );
    }

    private static DocumentationParse parseDocumentation(
            String[] lines,
            int startLineIndex,
            String sourceName,
            List<Diagnostic> diagnostics
    ) {
        String line = lines[startLineIndex];
        String trimmed = line.trim();
        SourceLocation location = new SourceLocation(
                sourceName,
                startLineIndex + 1,
                firstNonWhitespaceColumn(line)
        );

        int separator = trimmed.indexOf('=');
        if (separator < 0) {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Documentation declaration requires '='.",
                    location
            ));
            return new DocumentationParse(null, startLineIndex);
        }

        String key = trimmed.substring(0, separator).trim();
        String rawValue = trimmed.substring(separator + 1).trim();

        DocumentationTarget target = parseDocumentationTarget(
                key,
                location,
                diagnostics
        );

        if (target == null) {
            return new DocumentationParse(null, startLineIndex);
        }

        String text;
        int lastLineIndex = startLineIndex;

        if (rawValue.startsWith("\"\"\"")) {
            String afterOpen = rawValue.substring(3);

            if (afterOpen.endsWith("\"\"\"") && afterOpen.length() >= 3) {
                text = afterOpen.substring(0, afterOpen.length() - 3);
            } else {
                StringBuilder builder = new StringBuilder();

                if (!afterOpen.isEmpty()) {
                    builder.append(afterOpen);
                }

                boolean closed = false;

                for (int index = startLineIndex + 1; index < lines.length; index++) {
                    String current = lines[index];
                    int closeIndex = current.indexOf("\"\"\"");

                    if (closeIndex >= 0) {
                        String beforeClose = current.substring(0, closeIndex);

                        if (!beforeClose.isEmpty()) {
                            if (!builder.isEmpty()) {
                                builder.append('\n');
                            }
                            builder.append(beforeClose);
                        }

                        lastLineIndex = index;
                        closed = true;

                        if (!current.substring(closeIndex + 3).trim().isEmpty()) {
                            diagnostics.add(Diagnostic.error(
                                    "JCA001",
                                    "Unexpected content after closing triple quote.",
                                    new SourceLocation(sourceName, index + 1, closeIndex + 4)
                            ));
                        }
                        break;
                    }

                    if (!builder.isEmpty()) {
                        builder.append('\n');
                    }
                    builder.append(current);
                    lastLineIndex = index;
                }

                if (!closed) {
                    diagnostics.add(Diagnostic.error(
                            "JCA001",
                            "Unterminated multiline documentation string.",
                            location
                    ));
                    return new DocumentationParse(null, lastLineIndex);
                }

                text = builder.toString();
            }
        } else {
            ParsedValue parsed = parseSingleValue(
                    rawValue,
                    location,
                    diagnostics
            );

            if (parsed == null || !parsed.quoted()) {
                diagnostics.add(Diagnostic.error(
                        "JCA001",
                        "Documentation text must be quoted.",
                        location
                ));
                return new DocumentationParse(null, startLineIndex);
            }

            text = parsed.text();
        }

        return new DocumentationParse(
                new DocumentationDeclaration(
                        target.targetType(),
                        target.target(),
                        target.field(),
                        text,
                        location
                ),
                lastLineIndex
        );
    }

    private static DocumentationTarget parseDocumentationTarget(
            String key,
            SourceLocation location,
            List<Diagnostic> diagnostics
    ) {
        if (key.equals("doc.summary")) {
            return new DocumentationTarget(
                    DocumentationDeclaration.TargetType.ROOT,
                    Optional.empty(),
                    DocumentationDeclaration.Field.SUMMARY
            );
        }

        if (key.equals("doc.description")) {
            return new DocumentationTarget(
                    DocumentationDeclaration.TargetType.ROOT,
                    Optional.empty(),
                    DocumentationDeclaration.Field.DESCRIPTION
            );
        }

        if (!key.startsWith("doc.")) {
            diagnostics.add(Diagnostic.error(
                    "JCA503",
                    "Invalid documentation field.",
                    location
            ));
            return null;
        }

        String body = key.substring("doc.".length());
        int firstDot = body.indexOf('.');
        int lastDot = body.lastIndexOf('.');

        if (firstDot <= 0 || lastDot <= firstDot) {
            diagnostics.add(Diagnostic.error(
                    "JCA503",
                    "Invalid documentation field.",
                    location
            ));
            return null;
        }

        String type = body.substring(0, firstDot);
        String target = body.substring(firstDot + 1, lastDot);
        String field = body.substring(lastDot + 1);

        DocumentationDeclaration.TargetType targetType = switch (type) {
            case "command" -> DocumentationDeclaration.TargetType.COMMAND;
            case "option" -> DocumentationDeclaration.TargetType.OPTION;
            case "argument" -> DocumentationDeclaration.TargetType.ARGUMENT;
            default -> null;
        };

        if (targetType == null || target.isBlank()) {
            diagnostics.add(Diagnostic.error(
                    "JCA503",
                    "Invalid documentation target.",
                    location
            ));
            return null;
        }

        DocumentationDeclaration.Field docField = switch (field) {
            case "summary" -> DocumentationDeclaration.Field.SUMMARY;
            case "description" -> DocumentationDeclaration.Field.DESCRIPTION;
            default -> null;
        };

        if (docField == null) {
            diagnostics.add(Diagnostic.error(
                    "JCA503",
                    "Invalid documentation field: " + field,
                    location
            ));
            return null;
        }

        return new DocumentationTarget(
                targetType,
                Optional.of(target),
                docField
        );
    }

    private static List<String> parseSimpleBracedList(
            String body,
            SourceLocation location,
            List<Diagnostic> diagnostics
    ) {
        if (!body.startsWith("{") || !body.endsWith("}")) {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Expected a braced list.",
                    location
            ));
            return null;
        }

        List<ParsedValue> values = parseCsv(
                body.substring(1, body.length() - 1),
                location,
                diagnostics
        );

        if (values == null) {
            return null;
        }

        return values.stream()
                .map(ParsedValue::text)
                .toList();
    }

    private static List<ParsedValue> parseCsv(
            String body,
            SourceLocation location,
            List<Diagnostic> diagnostics
    ) {
        List<ParsedValue> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        boolean escaping = false;
        boolean currentWasQuoted = false;

        for (int index = 0; index < body.length(); index++) {
            char ch = body.charAt(index);

            if (escaping) {
                appendEscaped(current, ch);
                escaping = false;
                continue;
            }

            if (quoted && ch == '\\') {
                escaping = true;
                continue;
            }

            if (ch == '"') {
                quoted = !quoted;
                currentWasQuoted = true;
                continue;
            }

            if (ch == ',' && !quoted) {
                values.add(new ParsedValue(
                        current.toString().trim(),
                        currentWasQuoted
                ));
                current.setLength(0);
                currentWasQuoted = false;
                continue;
            }

            current.append(ch);
        }

        if (escaping || quoted) {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Unterminated quoted value.",
                    location
            ));
            return null;
        }

        if (!body.isEmpty() || !values.isEmpty()) {
            values.add(new ParsedValue(
                    current.toString().trim(),
                    currentWasQuoted
            ));
        }

        return values;
    }

    private static ParsedValue parseSingleValue(
            String rawValue,
            SourceLocation location,
            List<Diagnostic> diagnostics
    ) {
        if (rawValue.isEmpty()) {
            return new ParsedValue("", false);
        }

        if (!rawValue.startsWith("\"")) {
            if (rawValue.chars().anyMatch(Character::isWhitespace)) {
                diagnostics.add(Diagnostic.error(
                        "JCA001",
                        "Values containing whitespace must be quoted.",
                        location
                ));
                return null;
            }
            return new ParsedValue(rawValue, false);
        }

        if (rawValue.length() < 2 || !rawValue.endsWith("\"")) {
            diagnostics.add(Diagnostic.error(
                    "JCA001",
                    "Unterminated quoted value.",
                    location
            ));
            return null;
        }

        String body = rawValue.substring(1, rawValue.length() - 1);
        StringBuilder value = new StringBuilder();
        boolean escaping = false;

        for (int index = 0; index < body.length(); index++) {
            char ch = body.charAt(index);

            if (escaping) {
                appendEscaped(value, ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == '"') {
                diagnostics.add(Diagnostic.error(
                        "JCA001",
                        "Unescaped quote inside quoted value.",
                        location
                ));
                return null;
            } else {
                value.append(ch);
            }
        }

        if (escaping) {
            value.append('\\');
        }

        return new ParsedValue(value.toString(), true);
    }

    private static void appendEscaped(StringBuilder target, char ch) {
        switch (ch) {
            case '"' -> target.append('"');
            case '\\' -> target.append('\\');
            case 'n' -> target.append('\n');
            case 't' -> target.append('\t');
            default -> target.append('\\').append(ch);
        }
    }

    private static int findClosingBrace(String text, int openingIndex) {
        boolean quoted = false;
        boolean escaping = false;

        for (int index = openingIndex + 1; index < text.length(); index++) {
            char ch = text.charAt(index);

            if (escaping) {
                escaping = false;
                continue;
            }

            if (quoted && ch == '\\') {
                escaping = true;
                continue;
            }

            if (ch == '"') {
                quoted = !quoted;
                continue;
            }

            if (ch == '}' && !quoted) {
                return index;
            }
        }

        return -1;
    }

    private static int firstNonWhitespaceColumn(String line) {
        for (int index = 0; index < line.length(); index++) {
            if (!Character.isWhitespace(line.charAt(index))) {
                return index + 1;
            }
        }
        return 1;
    }

    public record ParseResult(
            JcaDocument document,
            List<Diagnostic> diagnostics
    ) {
        public ParseResult {
            Objects.requireNonNull(document, "document");
            Objects.requireNonNull(diagnostics, "diagnostics");
            diagnostics = List.copyOf(diagnostics);
        }

        public boolean hasErrors() {
            return diagnostics.stream().anyMatch(Diagnostic::isError);
        }
    }

    private record ParsedValue(String text, boolean quoted) {
    }

    private record DocumentationParse(
            DocumentationDeclaration declaration,
            int lastLineIndex
    ) {
    }

    private record DocumentationTarget(
            DocumentationDeclaration.TargetType targetType,
            Optional<String> target,
            DocumentationDeclaration.Field field
    ) {
    }
}
