# JCmdArgs Public API

This document describes the intentionally small public API of the Java 21 JCmdArgs core.

The recommended entry point is:

```java
com.hardcodacii.jcmdargs.JCmdArgs
```

The facade exposes four operations:

1. compile a `.jca` contract;
2. parse command-line arguments using a compiled `CliSpec`;
3. render concise help;
4. render a longer manual.

The core does not print, log or write generated documentation automatically.

---

## 1. Compile a contract

### From a file

```java
var compilation = JCmdArgs.compile(Path.of("application.jca"));
```

This overload may throw `IOException` because filesystem access is explicit.

### From an in-memory string

```java
var compilation = JCmdArgs.compile(jcaContent);
```

The internal source name is `<memory>`.

### From an in-memory string with a source name

```java
var compilation = JCmdArgs.compile(
        "application.jca",
        jcaContent
);
```

A meaningful source name improves diagnostic locations.

### From `JcaSource`

```java
var compilation = JCmdArgs.compile(
        JcaSource.of("application.jca", jcaContent)
);
```

---

## 2. `CompilationResult`

Compilation returns:

```java
CompilationResult
```

It contains:

```java
Optional<CliSpec> cliSpec();
List<Diagnostic> diagnostics();
boolean success();
boolean hasErrors();
```

On successful compilation, `cliSpec()` is present.

On failed compilation, `cliSpec()` is empty and the diagnostics contain at least one error.

Example:

```java
var compilation = JCmdArgs.compile("application.jca", jcaContent);

if (!compilation.success()) {
    for (var diagnostic : compilation.diagnostics()) {
        System.err.println(diagnostic);
    }
    return;
}

CliSpec spec = compilation.cliSpec().orElseThrow();
```

The decision to print diagnostics belongs to the consuming application, not to JCmdArgs.

---

## 3. `CliSpec`

`CliSpec` is the immutable semantic representation produced by the compiler.

It owns:

- optional command definition;
- option definitions;
- positional argument definitions;
- optional order policy;
- documentation;
- exact option alias index.

Examples:

```java
spec.command();
spec.options();
spec.arguments();
spec.orderPolicy();
spec.documentation();

spec.option("--long-form");
spec.option("-lf");

spec.argument("source");
```

For:

```properties
option={--long-form,-lf}
```

both alias lookups resolve the same logical `OptionSpec`.

`CliSpec` is safe to reuse for multiple parsing operations because the parser keeps invocation state locally.

---

## 4. Parse command-line arguments

```java
var result = JCmdArgs.parse(
        spec,
        "clone",
        "-lf",
        "--config",
        "application.properties",
        "https://example.test/repository.git"
);
```

The parser never re-reads the `.jca` source. Runtime parsing operates exclusively on `CliSpec`.

---

## 5. `ParseResult`

`ParseResult` contains:

```java
Optional<ParsedCommandLine> commandLine();
List<Diagnostic> diagnostics();
boolean success();
boolean hasErrors();
```

Example:

```java
if (!result.success()) {
    result.diagnostics().forEach(System.err::println);
    return;
}

var commandLine = result.commandLine().orElseThrow();
```

---

## 6. `ParsedCommandLine`

The immutable parsed result exposes:

```java
Optional<String> command();

List<ParsedOption> options();

boolean hasOption(String alias);

Optional<ParsedOption> option(String alias);

Optional<String> optionValue(String alias);

Map<String, String> arguments();

Optional<String> argument(String name);
```

Example:

```java
String command = commandLine.command().orElseThrow();

boolean longForm = commandLine.hasOption("--long-form");

String config = commandLine
        .optionValue("--config")
        .orElseThrow();

String source = commandLine
        .argument("source")
        .orElseThrow();

String destination = commandLine
        .argument("destination")
        .orElseThrow();
```

All aliases declared for one option resolve to the same parsed occurrence.

---

## 7. Help rendering

```java
String help = JCmdArgs.help(spec);
```

or with an explicit program name:

```java
String help = JCmdArgs.help(spec, "mytool");
```

Help is derived from `CliSpec`.

The renderer can include:

- usage shape;
- command;
- aliases;
- required/standalone markers;
- allowed values;
- implicit values;
- argument required/default information;
- authored summaries.

The method returns a `String`. It does not print or create files.

---

## 8. Manual rendering

```java
String manual = JCmdArgs.manual(spec);
```

or:

```java
String manual = JCmdArgs.manual(spec, "mytool");
```

The manual uses the same semantic model and includes authored descriptions in addition to concise structural information.

The method returns a `String`.

---

## 9. Diagnostics

A diagnostic contains:

```java
String code();
DiagnosticSeverity severity();
String message();
Optional<SourceLocation> sourceLocation();
```

Expected invalid input is represented through diagnostics rather than exceptions.

Exceptions are reserved for programmer errors or system failures, such as:

- passing `null` where the API forbids it;
- invalid direct construction of semantic model objects;
- filesystem I/O failure in `compile(Path)`.

---

## 10. Threading and reuse

The public semantic model is immutable.

`JcaCompiler` does not keep mutable compilation state between invocations.

`CliParser` keeps parsing state per invocation.

A successfully compiled `CliSpec` can therefore be retained and reused rather than recompiling the `.jca` contract for each command line.

A typical application lifecycle is:

```text
application startup
        |
        v
compile .jca once
        |
        v
retain CliSpec
        |
        +---- parse invocation 1
        |
        +---- parse invocation 2
        |
        +---- render help
        |
        +---- render manual
```

---

## 11. Dependency policy

The JCmdArgs core has zero runtime dependencies.

JUnit is a test-only dependency.

No framework, dependency injection container or logging implementation is required by the library.

---

## 12. Compatibility boundary

The supported public surface is centered on:

```text
com.hardcodacii.jcmdargs.JCmdArgs
com.hardcodacii.jcmdargs.api.*
com.hardcodacii.jcmdargs.diagnostic.*
com.hardcodacii.jcmdargs.jca.*
com.hardcodacii.jcmdargs.model.*
com.hardcodacii.jcmdargs.runtime.*
com.hardcodacii.jcmdargs.render.*
```

Packages containing `.internal` are implementation details and should not be treated as stable public API.

The JCA 1.0 language contract is documented separately in:

- `docs/jca-1.0-specification.md`
- `docs/jca-1.0-test-matrix.md`
