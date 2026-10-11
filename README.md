# JCmdArgs

**JCmdArgs is a schema-first command-line interface engine for Java.**

Define the syntax, validation rules, positional arguments and user-facing documentation of a CLI in one external `.jca` contract. JCmdArgs compiles that contract into an immutable `CliSpec`, then uses the same model for runtime parsing, diagnostics, help and manual rendering.

```text
.jca contract
     |
     v
 JcaCompiler
     |
     v
 immutable CliSpec
   /      |       \
  v       v        v
parse    help     manual
```

## Why JCmdArgs?

Many Java CLI libraries define a command line through annotations or programmatic builders. JCmdArgs takes a different approach: the CLI contract lives outside application code.

A single `.jca` file can describe:

- commands;
- option aliases;
- required and standalone options;
- fixed or free option values;
- implicit option values;
- named positional arguments;
- required, optional and defaulted positional arguments;
- argument ordering policy;
- application, command, option and argument documentation.

The compiled model is immutable and can be reused for multiple parses.

## Current baseline

The current Java 21 rewrite provides:

- Java 21;
- Maven build;
- zero runtime dependencies;
- immutable semantic model;
- `.jca` compiler and semantic validation;
- structured diagnostics;
- runtime CLI parser;
- help renderer;
- manual renderer;
- public `JCmdArgs` facade;
- no Spring dependency;
- no logging dependency;
- no implicit filesystem writes.

JUnit is used only for tests.

## Requirements

- JDK 21+
- Maven, or the included Maven Wrapper

Build and run the test suite:

### Windows PowerShell

```powershell
.\mvnw.cmd clean test
```

### Linux / macOS

```bash
./mvnw clean test
```

## A small `.jca` contract

```properties
arguments_number=1

option=!{--help,-h}
option={--verbose,-v}

argument=input=#

doc.summary="Example command-line tool."
doc.option.--help.summary="Show help and exit."
doc.option.--verbose.summary="Enable verbose output."
doc.argument.input.summary="Input file to process."
```

The contract states that:

- `--help` and `-h` are aliases for one standalone option;
- `--verbose` and `-v` are aliases for one flag;
- `input` is a required positional argument;
- help text is defined in the same contract.

See [`examples/minimal.jca`](examples/minimal.jca).

## Representative contract

A more complete example:

```properties
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

doc.summary="Repository clone utility."
doc.description="""
Clone a repository into a destination directory using a declarative
command-line contract.
"""

doc.command.clone.summary="Clone a repository."
doc.option.--help.summary="Show help and exit."
doc.option.--debug.summary="Control debug output."
doc.option.--long-form.summary="Use the long output form."
doc.option.--config.summary="Configuration file to use."
doc.option.--print-to.summary="Write output to a file."
doc.argument.source.summary="Source repository or directory."
doc.argument.destination.summary="Destination directory."
```

See [`examples/clone.jca`](examples/clone.jca).

## Java API

Compile a contract:

```java
import com.hardcodacii.jcmdargs.JCmdArgs;

import java.nio.file.Path;

var compilation = JCmdArgs.compile(Path.of("examples/clone.jca"));

if (!compilation.success()) {
    compilation.diagnostics().forEach(System.err::println);
    return;
}

var spec = compilation.cliSpec().orElseThrow();
```

Parse a command line using the compiled model:

```java
var result = JCmdArgs.parse(
        spec,
        "clone",
        "-lf",
        "--config",
        "application.properties",
        "https://example.test/repository.git"
);

if (!result.success()) {
    result.diagnostics().forEach(System.err::println);
    return;
}

var commandLine = result.commandLine().orElseThrow();

System.out.println(commandLine.command().orElseThrow());
System.out.println(commandLine.hasOption("--long-form"));
System.out.println(commandLine.optionValue("--config").orElseThrow());
System.out.println(commandLine.argument("source").orElseThrow());
System.out.println(commandLine.argument("destination").orElseThrow());
```

Aliases resolve to the same logical option:

```java
commandLine.hasOption("--long-form");
commandLine.hasOption("-lf");
```

Both expressions refer to the same option occurrence.

Render help or a manual from the same `CliSpec`:

```java
String help = JCmdArgs.help(spec, "mytool");
String manual = JCmdArgs.manual(spec, "mytool");
```

JCmdArgs returns strings; it does not print them or write files automatically.

For the public API overview, see [`docs/public-api.md`](docs/public-api.md).

## JCA 1.0 quick reference

### Options

Flag:

```properties
option={--verbose,-v}
```

Standalone option:

```properties
option=!{--help,-h}
```

A standalone option is intended for terminal operations such as help or version and may bypass otherwise required command-line elements.

Required option with a free value:

```properties
option=+{--config,-c}=#
```

Enum option with an implicit value:

```properties
option={--debug,-d}={disable,enable}
```

When present without an explicit value, the first declared value is the implicit value:

```text
--debug          -> disable
--debug=enable   -> enable
```

Enum option whose value is mandatory:

```properties
option={--mode,-m}=#{fast,slow}
```

Free value with an implicit value:

```properties
option={--print-to,-pt}={Logs.txt,#}
```

```text
--print-to          -> Logs.txt
--print-to=out.log  -> out.log
```

Option aliases are atomic exact matches. For example:

```properties
option={--long-form,-lf}
```

means that `-lf` is one alias. It is not interpreted as `-l -f`.

### Positional arguments

Required:

```properties
argument=source=#
```

Optional without a default:

```properties
argument=destination=?
```

Defaulted:

```properties
argument=destination=repository
```

A quoted special character is a literal:

```properties
argument=value="#"
```

### Argument count assertion

```properties
arguments_number=2
```

`arguments_number` is optional. When present, it is a compile-time assertion and must match the number of `argument=` declarations.

### Order policy

```properties
allowed_arguments_order={command,option,argument}
```

When omitted, ordering is flexible where parsing remains unambiguous.

### End of options

At runtime, the standard end-of-options marker is supported:

```text
--
```

After `--`, remaining tokens are interpreted as positional arguments even when they start with `-`.

## Documentation

Documentation is explicitly bound to semantic targets:

```properties
doc.summary="Repository clone utility."
doc.description="Longer application description."

doc.command.clone.summary="Clone a repository."
doc.option.--help.summary="Show help and exit."
doc.argument.source.summary="Source repository."
```

For an option such as:

```properties
option={--long-form,-lf}
```

documentation targets the primary alias:

```properties
doc.option.--long-form.summary="Use the long output form."
```

The secondary alias `-lf` is still a valid runtime alias, but it is not a separate documentation identity.

The renderers derive structural facts such as aliases, required markers, allowed values, implicit values and defaults from `CliSpec`. Those facts do not need to be repeated in prose.

## Diagnostics

Invalid contracts and invalid command lines return structured diagnostics rather than printing or logging automatically.

A diagnostic contains:

- stable code;
- severity;
- message;
- optional source location.

Examples of compiler codes:

```text
JCA103  option alias collision
JCA203  argument count mismatch
JCA501  unknown documentation target
```

Examples of runtime codes:

```text
CLI001  unknown option
CLI005  required option missing
CLI103  required command missing
CLI201  standalone option conflict
```

The full language and diagnostic contract is documented in:

- [`docs/jca-1.0-specification.md`](docs/jca-1.0-specification.md)
- [`docs/jca-1.0-test-matrix.md`](docs/jca-1.0-test-matrix.md)

## Design documents

- [`docs/jca-1.0-specification.md`](docs/jca-1.0-specification.md) — language and behavior contract
- [`docs/jca-1.0-test-matrix.md`](docs/jca-1.0-test-matrix.md) — acceptance and diagnostic matrix
- [`docs/jca-1.0-architecture.md`](docs/jca-1.0-architecture.md) — Java 21 architecture
- [`docs/future-roadmap.md`](docs/future-roadmap.md) — deliberately deferred features and product direction

## Scope of JCA 1.0

The current baseline deliberately does not attempt to implement every CLI feature.

Examples of deferred capabilities include:

- nested subcommands;
- repeatable options;
- mutually exclusive option groups;
- environment/config fallback;
- shell completion;
- response files;
- typed/constrained values;
- reusable `.jca` fragments;
- IDE/LSP tooling.

These belong to the roadmap rather than the JCA 1.0 core.

## Project status

The Java 21 core rewrite is functional and covered by the repository test suite.

The project has not yet declared a stable `1.0.0` release. Maven publication coordinates and release metadata should be considered provisional until that release is prepared.

## License

JCmdArgs is distributed under the **JCmdArgs Community License 1.0**.

The license permits broad use, study, modification, and redistribution while
requiring attribution to Hardcodacii. Before the first commercial distribution
of JCmdArgs, a derivative, or a product that commercially distributes JCmdArgs,
the distributor must send an informational notification to
`hardcodacii@hotmail.com`.

Internal use by a company does not by itself trigger the notification
requirement.

This is a source-available license and is not represented as an OSI-approved
open-source license. See [`LICENSE`](LICENSE) for the complete terms and
[`NOTICE`](NOTICE) for attribution information.
