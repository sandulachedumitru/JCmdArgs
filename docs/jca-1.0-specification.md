# JCmdArgs — JCA 1.0 Specification Draft

**Date:** 2026-10-11  
**Status:** Draft 0.3 — language contract consolidation  
**Java baseline:** Java 21  
**Scope:** JCmdArgs core DSL, documentation model and runtime parsing contract

## 1. Purpose

JCmdArgs 1.0 defines a command-line interface declaratively in a single `.jca` file. The file is intended to be the single source of truth for:

- command-line grammar;
- options and their Linux-style aliases;
- positional arguments;
- validation rules;
- help summaries;
- detailed manual text.

The JCA compiler converts the source into an immutable semantic model (`CliSpec`). Runtime parsing then interprets `String[] args` against that model and returns an immutable `ParseResult`.

## 2. Core technical constraints

The rewrite targets Java 21.

The JCmdArgs core must:

- require no framework;
- have zero required runtime dependencies if practical;
- have no Spring Boot dependency;
- have no `logs-indentation` dependency;
- perform no logging or console output during compilation/parsing;
- perform no filesystem writes as a side effect of compilation/parsing;
- keep DSL parsing, semantic validation, runtime parsing and rendering separate;
- return diagnostics as structured data;
- use immutable semantic models;
- be deterministic and thread-safe by design.

## 3. Processing model

```text
.jca source
   |
   v
JCA lexer/parser
   |
   v
syntax model
   |
   v
semantic compiler / validator
   |
   v
immutable CliSpec
   |               |                |
   v               v                v
runtime parser   HelpRenderer    ManualRenderer
   |
   v
immutable ParseResult
```

JCmdArgs core does not execute application business actions.

## 4. File rules

- Encoding is UTF-8.
- Blank lines are ignored.
- A comment line starts with `#` after optional leading whitespace.
- Inline comments are not part of JCA 1.0.
- Directive names are case-sensitive.
- Option, command and value matching is case-sensitive.
- Leading/trailing whitespace around complete directives may be ignored.
- Structural syntax outside quoted strings remains compact and whitespace-free.
- Double-quoted scalars support spaces and reserved characters.
- Triple-quoted text is available for multiline documentation.

The token `#` is context-sensitive:

- at the first non-whitespace position of a line: comment;
- as an unquoted value marker: required/free user-provided value;
- inside quotes: literal `#`.

The token `?` is reserved in argument value position for an optional positional argument without a default value. A quoted `"?"` is a literal question mark.

## 5. JCA directives

JCA 1.0 supports these structural directives:

```text
allowed_arguments_order
arguments_number
command
option
argument
```

and these documentation directives:

```text
doc.summary
doc.description
doc.command.<command>.summary
doc.command.<command>.description
doc.option.<exact-option-alias>.summary
doc.option.<exact-option-alias>.description
doc.argument.<argument-name>.summary
doc.argument.<argument-name>.description
```

Documentation is optional unless a future publication-validation profile explicitly requires it.

## 6. Informal grammar

```ebnf
document                   = { blank-line | comment-line | directive | documentation } ;

directive                  = order-directive
                           | arguments-number-directive
                           | command-directive
                           | option-directive
                           | argument-directive ;

order-directive            = "allowed_arguments_order=" category-list ;
arguments-number-directive = "arguments_number=" nonnegative-integer ;
category-list              = "{" category { "," category } "}" ;
category                   = "command" | "option" | "argument" ;

command-directive          = "command=" command-name ;

option-directive           = "option=" [ option-presence-modifier ] alias-list
                             [ "=" option-value-spec ] ;
option-presence-modifier   = "!" | "+" ;
alias-list                 = "{" option-alias [ "," option-alias ] "}" ;

option-value-spec          = "#"
                           | "#{" scalar "," scalar { "," scalar } "}"
                           | "{" scalar ",#}"
                           | "{" scalar "," scalar { "," scalar } "}" ;

argument-directive         = "argument=" argument-name "=" argument-value-spec ;
argument-value-spec        = "#" | "?" | scalar ;

documentation              = root-doc | target-doc ;
root-doc                   = "doc." doc-field "=" doc-text ;
target-doc                 = "doc." doc-kind "." doc-target "." doc-field "=" doc-text ;
doc-kind                   = "command" | "option" | "argument" ;
doc-field                  = "summary" | "description" ;
```

The grammar is intentionally compact. Repeatable options and nested subcommands are not included in the JCA 1.0 baseline.

## 7. Lexical forms

### 7.1 Commands

Recommended grammar:

```text
[A-Za-z][A-Za-z0-9-]*
```

Examples:

```text
clone
status
check-config
```

### 7.2 Argument names

Argument names are stable logical identifiers:

```text
[A-Za-z][A-Za-z0-9_]*
```

Examples:

```text
source
output
input_file
```

### 7.3 Linux-style option aliases

Option prefixes are part of the public CLI syntax and are never removed from the `.jca` definition or runtime input.

A long option begins with `--`:

```text
--debug
--long-form
--print-to
```

A short alias begins with exactly one `-` and may contain one or more characters:

```text
-d
-lf
-pt
```

Thus this is valid and intentional:

```properties
option={--long-form,-lf}
```

`-lf` is one atomic alias for `--long-form`. It is **not** automatically interpreted as `-l -f`.

JCA 1.0 does not implement automatic short-option clustering. If `-l` and `-f` are independently defined, `-lf` is not accepted unless `-lf` is explicitly declared as an alias.

A definition may have one alias or one long + one short alias. Aliases are globally unique.

Internally an implementation may normalize an option to a key, but this is never a change to the user-visible syntax.

### 7.4 Scalars

An unquoted scalar cannot contain whitespace or structural characters used by the surrounding grammar.

Double-quoted values may contain whitespace and reserved characters.

At minimum:

```text
\"  -> literal quote
\\  -> literal backslash
```

Examples:

```text
Logs.txt
repository
"C:\Program Files\Example\input.txt"
"#"
"?"
```

### 7.5 Multiline documentation text

Detailed descriptions may use triple quotes:

```properties
doc.option.--long-form.description="""
Selects the long output form.
This text may span multiple lines.
"""
```

Triple-quoted content is documentation text, not structural JCA syntax.

## 8. Option semantics

### 8.1 Flag

```properties
option={--verbose,-v}
```

The option accepts no value.

Runtime examples:

```text
--verbose
-v
```

Both refer to the same logical option.

`--verbose=value` is invalid.

### 8.2 Standalone option (`!`)

```properties
option=!{--help,-h}
```

A standalone option is intended for behavior such as help or version.

Rules:

- it may coexist with the configured command token;
- it may not coexist with any other option;
- it may not coexist with positional arguments;
- required positional arguments are bypassed;
- required options are bypassed;
- only one standalone option may appear in an invocation.

Example:

```text
app clone --help
```

may be valid even when normal execution requires options or positional arguments.

### 8.3 Required option (`+`)

Proposed JCA 1.0 syntax:

```properties
option=+{--config,-c}=#
```

`+` means the option itself must occur in a normal invocation.

This is distinct from `#`:

- `+` controls whether the option must be present;
- `#` controls whether a value must be supplied when the option is present.

Therefore:

```properties
option=+{--config,-c}=#
```

means that `--config`/`-c` must occur and must have a value.

`!` and `+` are mutually exclusive modifiers.

### 8.4 Enum option with implicit value

```properties
option={--debug,-d}={disable,enable}
```

Semantics:

- allowed values: `disable`, `enable`;
- first value (`disable`) is the implicit value when the option appears without an explicit value;
- if the option is absent, it remains absent;
- the implicit value is not a global application default.

Runtime:

```text
--debug          -> disable
--debug=enable   -> enable
```

Because the option has an implicit value, separated explicit-value syntax is intentionally not used:

```text
--debug enable
```

is parsed as bare `--debug` followed by a separate semantic token. This avoids ambiguity with positional arguments.

### 8.5 Required enum value

JCA 1.0 proposes an explicit required-enum form:

```properties
option={--mode,-m}=#{fast,slow}
```

Semantics:

- option itself is optional unless prefixed by `+`;
- if option occurs, its value is required;
- allowed values are `fast` and `slow`;
- there is no implicit value.

Both forms are valid:

```text
--mode=fast
--mode fast
```

### 8.6 Free required value

```properties
option={--output,-o}=#
```

If the option appears, any scalar value is accepted and a value is mandatory.

Both forms are valid:

```text
--output=result.txt
--output result.txt
```

### 8.7 Free value with implicit value

```properties
option={--print-to,-pt}={Logs.txt,#}
```

Semantics:

```text
--print-to            -> Logs.txt
--print-to=Build.log  -> Build.log
```

Because the option has an implicit value, a separated explicit value is not consumed automatically. This keeps positional parsing deterministic.

### 8.8 Option absence

An option-level implicit value is applied **only when that option is present without an explicit value**.

If the option is absent:

- `ParseResult` reports it as absent;
- its implicit value is not injected globally.

This is deliberately different from positional argument defaults.

### 8.9 Repetition

JCA 1.0 options are scalar and non-repeatable.

These are errors:

```text
--debug --debug=enable
--debug -d
```

Repeatable options are reserved for a later language revision so their syntax and result type can be designed explicitly.

## 9. Positional argument semantics

### 9.1 Required argument

```properties
argument=source=#
```

The caller must supply a value.

### 9.2 Optional argument with no default

Proposed JCA 1.0 syntax:

```properties
argument=destination=?
```

The caller may omit the argument. If omitted, the result contains no value for that argument.

This is distinct from a defaulted positional argument.

### 9.3 Optional argument with default

```properties
argument=destination=repository
```

If omitted, `repository` becomes the effective positional value.

### 9.4 Positional assignment

Arguments are assigned left-to-right in declaration order.

Example:

```properties
argument=source=#
argument=destination=?
argument=mode=normal
```

A caller cannot skip `destination` syntactically and provide only `mode`; positional assignment always fills the earliest available position.

### 9.5 Declaration ordering

Required positional arguments must precede all optional/defaulted positional arguments.

Valid:

```properties
argument=source=#
argument=destination=?
argument=mode=normal
```

Invalid:

```properties
argument=destination=?
argument=source=#
```

### 9.6 Extra values

Runtime positional values beyond the declared positional argument count are errors.

Variadic positional arguments are not in the JCA 1.0 baseline.

## 10. `arguments_number`

`arguments_number` remains optional compile-time consistency metadata.

Example:

```properties
arguments_number=3
```

Rules:

- at most one declaration;
- value is a non-negative integer;
- value must exactly equal the number of `argument=` directives;
- mismatch is a compilation error;
- it does not control runtime parsing;
- it need not be retained in final `CliSpec`.

Example:

```properties
arguments_number=3
argument=source=#
argument=destination=?
```

must fail because the author declared an intention to define three positional arguments but only defined two.

Suggested diagnostic:

```text
JCA203 ARGUMENT_COUNT_MISMATCH
```

## 11. Commands

A JCA 1.0 file contains zero or one command.

```properties
command=clone
```

If defined, normal invocation requires the exact command token.

Nested commands/subcommands and multiple command branches are deliberately deferred beyond the initial 1.0 baseline. The internal model should nevertheless avoid design choices that make future subcommand trees impossible.

## 12. Argument order policy

`allowed_arguments_order` is optional.

Example:

```properties
allowed_arguments_order={command,option,argument}
```

When present, runtime input must obey the category phases.

When absent, parsing is flexible subject to semantic disambiguation rules and `--`.

The order declaration is a runtime policy, not merely a file validation hint.

## 13. End-of-options marker

Runtime parsing supports:

```text
--
```

After this marker all tokens are positional values, even if they begin with `-` or `--`.

Example:

```text
app -- --long-form
```

passes the literal `--long-form` as a positional value rather than recognizing it as an option.

## 14. Documentation model

Documentation lives in the same `.jca` file as grammar and validation rules.

This makes the file the authoritative source for runtime parsing, help and manual generation.

### 14.1 Root documentation

```properties
doc.summary="Repository clone utility."
doc.description="""
Clones a repository from a source to a destination.
The command-line grammar and its documentation are defined together.
"""
```

### 14.2 Command documentation

```properties
doc.command.clone.summary="Clone a repository."
doc.command.clone.description="""
Creates a local copy of the requested source.
"""
```

### 14.3 Option documentation

Documentation references an option by an **exact declared alias**, preferably its long alias:

```properties
option={--long-form,-lf}

doc.option.--long-form.summary="Use the long output form."
doc.option.--long-form.description="""
Displays output using the long form rather than the compact form.
"""
```

The source retains `--`; the documentation syntax does not hide that the target is an option.

If an option has both long and short aliases, documentation attached through either exact alias refers to the same logical option. Defining the same field twice through different aliases is a duplicate-documentation error.

### 14.4 Argument documentation

```properties
argument=source=#

doc.argument.source.summary="Source repository or directory."
doc.argument.source.description="""
The source is mandatory for normal execution.
"""
```

### 14.5 Documentation target resolution

Documentation may appear before or after the corresponding definition because target resolution happens during semantic compilation.

A documentation target that does not exist is an error:

```text
JCA501 UNKNOWN_DOCUMENTATION_TARGET
```

Duplicate `summary` or `description` for one logical target is an error:

```text
JCA502 DUPLICATE_DOCUMENTATION_FIELD
```

Unknown documentation fields are errors:

```text
JCA503 INVALID_DOCUMENTATION_FIELD
```

Missing documentation is not a normal compilation error. A future publication-validation profile may emit:

```text
JCA504 MISSING_DOCUMENTATION
```

as a warning.

### 14.6 Derived technical help

User documentation should explain intent, not duplicate syntax already known by the model.

For example, the help renderer derives automatically:

- aliases;
- whether a value is required;
- allowed enum values;
- implicit/default values;
- required/optional status;
- positional ordering.

Given:

```properties
option={--long-form,-lf}
doc.option.--long-form.summary="Use the long output form."
```

it may render:

```text
-lf, --long-form    Use the long output form.
```

This prevents help text from drifting away from the executable CLI contract.

## 15. Default and implicit value terminology

JCA 1.0 distinguishes two concepts that the legacy prototype mixed together.

### Option implicit value

```properties
option={--debug,-d}={disable,enable}
```

`disable` is an **implicit value** used only if `--debug` occurs without an explicit value.

It is not applied when the option is absent.

### Positional default value

```properties
argument=destination=repository
```

`repository` is a true **default value** used when the positional argument is omitted.

The Java semantic model should preserve this distinction with different field names.

## 16. Canonical example

```properties
# CLI structure
arguments_number=2
allowed_arguments_order={command,option,argument}

command=clone

option=!{--help,-h}
option={--debug,-d}={disable,enable}
option={--long-form,-lf}
option=+{--config,-c}=#
option={--print-to,-pt}={Logs.txt,#}

argument=source=#
argument=destination=repository

# General documentation
doc.summary="Repository clone utility."
doc.description="""
Clones a source repository into a destination directory.
"""

# Command documentation
doc.command.clone.summary="Clone a repository."

# Option documentation
doc.option.--help.summary="Show help and exit."
doc.option.--debug.summary="Control debug output."
doc.option.--long-form.summary="Use the long output form."
doc.option.--config.summary="Configuration file to use."
doc.option.--print-to.summary="Write output to a file."

# Argument documentation
doc.argument.source.summary="Source repository or directory."
doc.argument.destination.summary="Destination directory."
```

## 17. Example generated help

A renderer can derive output similar to:

```text
Repository clone utility.

Usage:
  app clone [options] <source> [destination]

Options:
  -h,  --help                 Show help and exit.
  -d,  --debug [value]        Control debug output. Values: disable, enable. Implicit: disable.
  -lf, --long-form            Use the long output form.
  -c,  --config <value>       Configuration file to use. Required.
  -pt, --print-to [value]     Write output to a file. Implicit: Logs.txt.

Arguments:
  source                      Source repository or directory. Required.
  destination                 Destination directory. Default: repository.
```

Exact formatting belongs to `HelpRenderer`, not to the JCA grammar.

## 18. Runtime result semantics

A successful parse should expose semantic data rather than raw parser artifacts.

Conceptually:

```java
ParseResult result = parser.parse(args);
```

The result should support:

- selected command;
- option presence;
- option values;
- positional values by name;
- distinction between absent optional argument and a defaulted argument;
- immutable collections/data.

Aliases resolve to one logical option. `--long-form` and `-lf` must never appear as two independent option entries.

## 19. Diagnostics

Diagnostics are data, not console output.

Conceptual Java 21 model:

```java
public record Diagnostic(
        String code,
        DiagnosticSeverity severity,
        String message,
        SourceLocation sourceLocation) {
}
```

Callers decide how diagnostics are displayed or logged.

## 20. Features deliberately deferred beyond the initial JCA 1.0 baseline

The following are not to be implemented accidentally during the first rewrite:

- nested subcommands;
- multiple command branches in one JCA;
- repeatable options;
- short-option clustering (`-abc` => `-a -b -c`);
- variadic positional arguments;
- typed conversion to Java application types;
- regex/range/custom constraints;
- mutually exclusive option groups beyond standalone `!`;
- environment-variable fallbacks;
- response files;
- shell completion generation;
- localization;
- Spring integration.

These remain compatible future directions if the semantic model stays clean.

## 21. Core semantic model direction

Conceptually:

```text
CliSpec
 |- Documentation
 |- CommandSpec?
 |- List<OptionSpec>
 |- List<ArgumentSpec>
 `- OrderPolicy?

OptionSpec
 |- aliases (including literal '-' / '--' prefixes)
 |- required
 |- standalone
 |- valueMode
 |- allowedValues
 |- implicitValue
 `- Documentation

ArgumentSpec
 |- name
 |- position
 |- required
 |- defaultValue?
 `- Documentation
```

Possible option value modes:

```text
NONE
ENUM_REQUIRED
ENUM_IMPLICIT
FREE_REQUIRED
FREE_IMPLICIT
```

The special DSL tokens `#`, `?`, `!`, `+`, `{}` do not propagate through runtime parsing. They are compiled once into explicit semantic fields.

## 22. Architectural consequence for legacy code

The rewrite should preserve recovered behavior, not legacy class structure.

The new Java 21 implementation should be built around immutable models and stateless compiler/parser components. Legacy Spring services, action performers, filesystem help generation and logging/display dependencies are not architectural constraints for the rewrite.

## 23. Decisions still requiring explicit approval before JCA 1.0 freeze

Draft 0.3 proposes two new syntax elements that should be treated as draft until accepted:

1. `+` before an option alias set means **required option**.
2. `?` in positional argument value position means **optional argument without default**.

It also proposes the required-enum syntax:

```properties
option={--mode,-m}=#{fast,slow}
```

Repeatable options and nested subcommands are deliberately deferred.

Once these items are accepted or adjusted, the JCA 1.0 language surface can be frozen for the Java 21 implementation baseline.
