# JCmdArgs — Java 21 Architecture Plan

**Status:** Proposed architecture before rewrite  
**Target branch:** `rewrite/jca-1.0-java21-core`  
**Java baseline:** Java 21  
**Build tool:** Maven  
**Core runtime dependencies target:** 0

---

## 1. Architectural objective

JCmdArgs 1.0 will be rewritten as a small, framework-independent Java 21 library centered on a declarative CLI contract.

```text
.jca source
    |
    v
JCA parser
    |
    v
semantic compiler / validator
    |
    v
immutable CliSpec
   /   |   \
  /    |    \
 v     v     v
runtime parser   help renderer   manual renderer
    |
    v
ParseResult
```

Core rules:

- Java 21.
- No Spring Boot.
- No `logs-indentation`.
- No Lombok.
- No mandatory runtime logging dependency.
- No console output from core.
- No filesystem writes from compile/parse/render operations.
- Immutable semantic model.
- Structured diagnostics.
- `.jca` is the single source of truth for CLI grammar, validation and documentation.
- Linux-style aliases remain explicit, e.g. `--long-form` and `-lf`.
- Multi-character aliases such as `-lf` are atomic aliases, not automatic short-option clusters.
- `arguments_number` is optional compile-time consistency metadata.
- Runtime parsing is separate from JCA compilation.

---

## 2. Rewrite strategy

Do not refactor the current implementation class by class.

Use a controlled strangler rewrite:

1. Keep the legacy implementation temporarily for reference.
2. Create the new Java 21 packages alongside it.
3. Build the new immutable model.
4. Build the new JCA compiler.
5. Pass the JCA test matrix.
6. Build the new runtime parser.
7. Pass runtime and end-to-end tests.
8. Build help/manual renderers.
9. Remove legacy implementation only after parity is demonstrated.
10. Update README and examples last.

No legacy class becomes a dependency of the new implementation.

---

## 3. Target package structure

```text
src/main/java/com/hardcodacii/jcmdargs/
│
├── JCmdArgs.java
│
├── api/
│   ├── CompilationResult.java
│   └── ParseResult.java
│
├── diagnostic/
│   ├── Diagnostic.java
│   ├── DiagnosticSeverity.java
│   └── SourceLocation.java
│
├── model/
│   ├── CliSpec.java
│   ├── CommandSpec.java
│   ├── OptionSpec.java
│   ├── ArgumentSpec.java
│   ├── Documentation.java
│   ├── ArgumentCategory.java
│   ├── OrderPolicy.java
│   ├── OptionValueSpec.java
│   ├── NoOptionValue.java
│   ├── EnumOptionValue.java
│   ├── FreeOptionValue.java
│   ├── ArgumentValueSpec.java
│   ├── RequiredArgumentValue.java
│   ├── OptionalArgumentValue.java
│   └── DefaultArgumentValue.java
│
├── jca/
│   ├── JcaCompiler.java
│   ├── JcaSource.java
│   └── internal/
│       ├── JcaParser.java
│       ├── JcaSemanticAnalyzer.java
│       ├── JcaDocument.java
│       ├── JcaStatement.java
│       ├── CommandDeclaration.java
│       ├── OptionDeclaration.java
│       ├── ArgumentDeclaration.java
│       ├── ArgumentsNumberDeclaration.java
│       ├── OrderDeclaration.java
│       ├── DocumentationDeclaration.java
│       └── SourceCursor.java
│
├── runtime/
│   ├── CliParser.java
│   ├── ParsedCommandLine.java
│   ├── ParsedOption.java
│   └── internal/
│       ├── CliToken.java
│       ├── CliTokenType.java
│       ├── CliTokenClassifier.java
│       └── ParseState.java
│
└── render/
    ├── HelpRenderer.java
    ├── ManualRenderer.java
    └── internal/
        └── UsageRenderer.java
```

Tests mirror these packages under `src/test/java`.

---

## 4. Root facade — `JCmdArgs`

**Class:** `com.hardcodacii.jcmdargs.JCmdArgs`  
**Visibility:** public, final  
**Role:** convenience facade for common usage.

Conceptual API:

```java
CompilationResult compile(String jca);
CompilationResult compile(Path jcaFile);

ParseResult parse(CliSpec spec, String... args);

String help(CliSpec spec);
String manual(CliSpec spec);
```

The exact API may use static convenience methods.

Rules:

- stateless;
- no DI/container lifecycle;
- no global mutable state;
- no stdout/stderr;
- no `System.exit`;
- no filesystem writes.

Advanced users can directly use `JcaCompiler`, `CliParser`, `HelpRenderer`, and `ManualRenderer`.

---

## 5. Result API

### `api.CompilationResult`

Recommended as a record:

```java
record CompilationResult(
    Optional<CliSpec> cliSpec,
    List<Diagnostic> diagnostics
)
```

Responsibilities:

- successful compiled `CliSpec`, when available;
- all compilation diagnostics;
- convenience methods such as `success()` and `hasErrors()`.

Invariant:

- if an ERROR diagnostic exists, `cliSpec` is empty;
- diagnostics are immutable.

### `api.ParseResult`

Recommended as a record:

```java
record ParseResult(
    Optional<ParsedCommandLine> commandLine,
    List<Diagnostic> diagnostics
)
```

Runtime user mistakes are diagnostics, not normal exception flow.

---

## 6. Diagnostics package

### `diagnostic.Diagnostic`

Recommended record.

Fields:

```text
code
severity
message
sourceLocation
```

Stable machine-readable codes include:

```text
JCA203
JCA501
CLI001
```

Messages may evolve while codes remain stable.

### `diagnostic.DiagnosticSeverity`

Enum:

```text
ERROR
WARNING
INFO
```

### `diagnostic.SourceLocation`

Recommended record.

Fields:

```text
sourceName
line
column
```

JCA diagnostics should preserve precise source locations whenever practical.

---

## 7. Semantic model boundary

The `model` package is the main architectural boundary.

Nothing below this boundary carries raw syntax markers such as:

```text
#
!
+
?
={}
```

Those symbols are interpreted once by the JCA compiler.

Runtime parsing and renderers consume semantic objects only.

---

## 8. `model.CliSpec`

Recommended as a final immutable class rather than a record because it should own validated lookup indexes.

Responsibilities:

- optional command specification;
- immutable options;
- immutable positional arguments;
- optional order policy;
- top-level documentation;
- exact alias lookup.

Derived internal index:

```text
alias -> OptionSpec
```

Thus:

```text
--long-form
-lf
```

resolve to the same `OptionSpec`.

Potential API:

```java
Optional<CommandSpec> command();
List<OptionSpec> options();
List<ArgumentSpec> arguments();
Optional<OrderPolicy> orderPolicy();
Documentation documentation();

Optional<OptionSpec> option(String alias);
```

Aliases are preserved exactly, including `-` and `--`.

---

## 9. `model.CommandSpec`

Recommended record.

Fields:

```text
name
documentation
```

JCA 1.0 supports at most one command per contract.

Nested subcommands stay in the future roadmap.

---

## 10. `model.OptionSpec`

Recommended record.

Fields:

```text
primaryAlias
aliases
required
standalone
valueSpec
documentation
```

Example:

```properties
option={--long-form,-lf}
```

becomes:

```text
primaryAlias = "--long-form"
aliases      = ["--long-form", "-lf"]
required     = false
standalone   = false
valueSpec    = NoOptionValue
```

Important:

The public identity stays `--long-form`, not `long-form`.

For an option without a long alias, the first declared alias becomes `primaryAlias`.

Aliases are exact and case-sensitive unless the language specification later changes that rule.

---

## 11. Option value model

Use Java 21 sealed types to prevent invalid field combinations.

### `model.OptionValueSpec`

Sealed interface permitting:

```text
NoOptionValue
EnumOptionValue
FreeOptionValue
```

### `model.NoOptionValue`

Fieldless record representing a flag:

```properties
option={--verbose,-v}
```

### `model.EnumOptionValue`

Recommended record.

Fields:

```text
allowedValues
implicitValue
```

For:

```properties
option={--debug,-d}={disable,enable}
```

the semantic form is:

```text
allowedValues = ["disable", "enable"]
implicitValue = "disable"
```

Therefore:

```text
--debug
```

means the option is present with `disable`.

Absence of `--debug` is still different from presence with the implicit value.

For:

```properties
option={--mode,-m}=#{fast,slow}
```

the semantic form is:

```text
allowedValues = ["fast", "slow"]
implicitValue = empty
```

and a value is mandatory when the option is present.

### `model.FreeOptionValue`

Recommended record.

Field:

```text
implicitValue
```

For:

```properties
option={--output,-o}=#
```

`implicitValue` is empty and a value is required.

For:

```properties
option={--print-to,-pt}={Logs.txt,#}
```

`implicitValue` is `Logs.txt`.

---

## 12. Positional argument model

### `model.ArgumentSpec`

Recommended record.

Fields:

```text
name
position
valueSpec
documentation
```

For:

```properties
argument=source=#
argument=destination=repository
```

the model is:

```text
source:
  position = 0
  valueSpec = RequiredArgumentValue

destination:
  position = 1
  valueSpec = DefaultArgumentValue("repository")
```

### `model.ArgumentValueSpec`

Sealed interface permitting:

```text
RequiredArgumentValue
OptionalArgumentValue
DefaultArgumentValue
```

### `model.RequiredArgumentValue`

Fieldless record representing:

```properties
argument=source=#
```

### `model.OptionalArgumentValue`

Fieldless record representing:

```properties
argument=destination=?
```

The semantic validator rejects ambiguous positional layouts.

### `model.DefaultArgumentValue`

Record with:

```text
defaultValue
```

representing:

```properties
argument=destination=repository
```

---

## 13. Documentation model

### `model.Documentation`

Recommended record.

Fields:

```text
summary
description
```

Both can be optional.

Examples:

```properties
doc.option.--long-form.summary="Use the long output form."
doc.argument.source.summary="Source repository."
```

Documentation is resolved during compilation and attached to the semantic object.

A successful `CliSpec` cannot contain dangling documentation targets.

---

## 14. Ordering model

### `model.ArgumentCategory`

Enum:

```text
COMMAND
OPTION
ARGUMENT
```

### `model.OrderPolicy`

Recommended record containing:

```text
List<ArgumentCategory> categories
```

Example:

```properties
allowed_arguments_order={command,option,argument}
```

becomes:

```text
[COMMAND, OPTION, ARGUMENT]
```

If absent, runtime token ordering is flexible.

---

## 15. `jca.JcaSource`

Recommended record.

Fields:

```text
name
content
```

Examples:

```text
name = "application.jca"
name = "<memory>"
```

Factories may include:

```java
JcaSource.of(String name, String content)
JcaSource.from(Path path)
```

This lets compilation work with files, strings, tests, and future editor buffers without coupling the parser to filesystem state.

Reading a source file is allowed. Writing is not a compiler responsibility.

---

## 16. `jca.JcaCompiler`

Public final class.

Main API:

```java
CompilationResult compile(JcaSource source)
```

Internal flow:

```text
JcaSource
  -> JcaParser
  -> JcaDocument
  -> JcaSemanticAnalyzer
  -> CliSpec
```

The compiler is stateless and naturally thread-safe.

No state is retained between compilations.

---

## 17. JCA internal syntax model

The following classes are implementation details.

### `jca.internal.JcaDocument`

Contains ordered:

```text
List<JcaStatement>
```

plus source metadata.

### `jca.internal.JcaStatement`

Sealed interface permitting:

```text
CommandDeclaration
OptionDeclaration
ArgumentDeclaration
ArgumentsNumberDeclaration
OrderDeclaration
DocumentationDeclaration
```

Every statement carries a `SourceLocation`.

### `CommandDeclaration`

Parsed representation of:

```properties
command=clone
```

### `OptionDeclaration`

Contains:

```text
required marker
standalone marker
aliases
parsed value declaration
source location
```

Examples parsed here include:

```properties
option=!{--help,-h}
option=+{--config,-c}=#
option={--debug,-d}={disable,enable}
option={--long-form,-lf}
```

Cross-option alias collisions are not checked here.

### `ArgumentDeclaration`

Contains:

```text
name
raw semantic form
source location
```

Examples:

```properties
argument=source=#
argument=destination=?
argument=destination=repository
```

### `ArgumentsNumberDeclaration`

Contains declared integer and source location.

`arguments_number` is consumed by semantic validation and is not copied into runtime `CliSpec`.

Its purpose is author consistency checking.

### `OrderDeclaration`

Parsed representation of:

```properties
allowed_arguments_order={command,option,argument}
```

### `DocumentationDeclaration`

Contains:

```text
target type
target identifier
field
text
source location
```

Examples:

```properties
doc.summary="..."
doc.option.--long-form.summary="..."
doc.argument.source.description="..."
```

### `SourceCursor`

A small parser-local helper tracking:

```text
offset
line
column
```

It must not become a general-purpose utility class.

---

## 18. `jca.internal.JcaParser`

Package-private final class.

Responsibilities:

- comments;
- whitespace;
- quoted strings;
- multiline strings;
- braces;
- commas;
- equals signs;
- `#`;
- `!`;
- `+`;
- `?`;
- definition keywords;
- source location tracking.

It produces syntax declarations and syntax diagnostics.

It does **not**:

- check collisions between separate options;
- compare `arguments_number` with actual arguments;
- resolve documentation targets;
- implement runtime parsing.

A standalone generic lexer is intentionally not required for the first rewrite.

If measured parser complexity justifies it later, parsing may be split internally without changing public API.

---

## 19. `jca.internal.JcaSemanticAnalyzer`

Package-private final class.

Responsibilities:

- duplicate options;
- alias collisions;
- alias syntax;
- atomic multi-character short aliases;
- command constraints;
- argument names and positions;
- positional ambiguity;
- `arguments_number` consistency;
- order policy;
- documentation target resolution;
- implicit/default value validation;
- construction of the immutable semantic model.

This replaces responsibilities previously scattered through:

```text
RuleServiceImpl
DefinitionOption
DefinitionNonOption
DefinitionParser
DefinitionPropertiesParser*
```

Initially prefer focused private methods rather than a class per validation rule.

Extract validators later only if complexity proves it necessary.

---

## 20. `runtime.CliParser`

Public final class.

API:

```java
ParseResult parse(CliSpec spec, String... args)
```

Responsibilities:

- exact alias lookup;
- command matching;
- flags;
- enum values;
- free values;
- positional arguments;
- defaults;
- standalone options;
- required options;
- `--` end-of-options marker;
- strict order policy;
- duplicate option policy;
- missing/extra arguments;
- structured CLI diagnostics.

It never reads `.jca`.

It only consumes `CliSpec`.

---

## 21. `runtime.ParsedCommandLine`

Final immutable class.

Contains:

```text
command
options
arguments
```

Suggested API:

```java
Optional<String> command();

boolean hasOption(String alias);
Optional<String> optionValue(String alias);

Optional<String> argument(String name);
```

Both:

```java
hasOption("--long-form")
hasOption("-lf")
```

address the same logical option.

Internally, parsed options are indexed by `OptionSpec.primaryAlias()`.

---

## 22. `runtime.ParsedOption`

Recommended record.

Fields:

```text
primaryAlias
matchedAlias
value
```

For invocation:

```text
-lf
```

against:

```properties
option={--long-form,-lf}
```

the result is:

```text
primaryAlias = "--long-form"
matchedAlias = "-lf"
value        = empty
```

This preserves semantic identity and the alias actually entered by the user.

---

## 23. Runtime internal classes

### `runtime.internal.CliToken`

One command-line token plus its original index.

### `runtime.internal.CliTokenType`

Potential enum:

```text
OPTION
VALUE
POSITIONAL
END_OF_OPTIONS
```

Classification remains context-aware.

After `--`, a token beginning with `-` is positional.

### `runtime.internal.CliTokenClassifier`

Performs lexical classification and exact alias recognition.

Exact declared aliases win.

Therefore:

```text
-lf
```

is the atomic alias `-lf` if declared.

There is no implicit split into `-l -f`.

### `runtime.internal.ParseState`

Mutable state local to one `parse()` invocation:

```text
token index
seen options
parsed arguments
current positional index
current order category
end-of-options state
```

No parse state is retained in `CliParser`.

It is converted to immutable `ParsedCommandLine` at the end.

---

## 24. Rendering package

### `render.HelpRenderer`

Public final class.

Produces concise terminal help using `CliSpec`.

It derives structural facts automatically:

- aliases;
- required;
- defaults;
- implicit values;
- enum values.

Example:

```text
-lf, --long-form     Use the long output form.
```

### `render.ManualRenderer`

Public final class.

Produces expanded plain-text documentation using summaries, descriptions and semantic metadata.

Markdown/man/HTML renderers remain roadmap items.

### `render.internal.UsageRenderer`

Single helper responsible for canonical usage lines:

```text
Usage:
  app clone [options] <source> [destination]
```

Shared by help and manual rendering.

---

## 25. Public API boundary

Supported public surface:

```text
com.hardcodacii.jcmdargs.JCmdArgs

com.hardcodacii.jcmdargs.api.*
com.hardcodacii.jcmdargs.diagnostic.*
com.hardcodacii.jcmdargs.model.*

com.hardcodacii.jcmdargs.jca.JcaCompiler
com.hardcodacii.jcmdargs.jca.JcaSource

com.hardcodacii.jcmdargs.runtime.CliParser
com.hardcodacii.jcmdargs.runtime.ParsedCommandLine
com.hardcodacii.jcmdargs.runtime.ParsedOption

com.hardcodacii.jcmdargs.render.HelpRenderer
com.hardcodacii.jcmdargs.render.ManualRenderer
```

Anything under `.internal` is explicitly unsupported implementation detail.

---

## 26. Exception policy

Do not recreate the current checked-exception hierarchy.

Expected invalid input belongs in diagnostics:

```text
invalid JCA
unknown option
missing option value
invalid enum value
missing positional argument
```

Exceptions are reserved for programmer/system failures.

Normal validation flow should not require catching exceptions.

---

## 27. Build architecture

Keep Maven.

Recommended coordinates:

```xml
<groupId>com.hardcodacii</groupId>
<artifactId>jcmdargs</artifactId>
```

Java baseline:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

Remove:

```text
spring-boot-starter-parent
spring-boot-starter
spring-boot-devtools
spring-boot-configuration-processor
spring-boot-starter-test
spring-boot-maven-plugin
logs-indentation
lombok
```

Runtime dependency target:

```text
0
```

Test dependency:

```text
JUnit Jupiter
```

Required build plugins should remain minimal, typically:

```text
maven-compiler-plugin
maven-surefire-plugin
```

---

## 28. Resource policy

Eventually remove framework/application resources:

```text
src/main/resources/application.yaml
src/main/resources/banner.txt
src/main/resources/cmd-general-rule.txt
```

Legacy help files become obsolete because documentation lives in `.jca`:

```text
cmd-line/help-files/*
src/main/resources/cmd-line/help-files/*
```

Useful old examples should be migrated explicitly into either:

```text
examples/
```

or:

```text
src/test/resources/jca/
```

depending on purpose.

Before deleting:

```text
remarks.txt
parseArgCases.txt
jcmdargs.txt
```

audit them one final time for unrecovered design knowledge and move valuable information into `/docs`.

---

## 29. Test architecture

```text
src/test/java/com/hardcodacii/jcmdargs/
├── jca/
│   ├── JcaParserTest.java
│   ├── JcaCompilerTest.java
│   ├── OptionValidationTest.java
│   ├── ArgumentValidationTest.java
│   ├── ArgumentsNumberValidationTest.java
│   ├── DocumentationValidationTest.java
│   └── OrderPolicyValidationTest.java
│
├── runtime/
│   ├── FlagParsingTest.java
│   ├── OptionValueParsingTest.java
│   ├── PositionalArgumentParsingTest.java
│   ├── StandaloneOptionParsingTest.java
│   ├── RequiredOptionParsingTest.java
│   ├── EndOfOptionsMarkerTest.java
│   ├── AtomicShortAliasTest.java
│   └── ArgumentOrderParsingTest.java
│
├── render/
│   ├── HelpRendererTest.java
│   └── ManualRendererTest.java
│
└── integration/
    └── JcaToParseResultTest.java
```

The formal JCA test matrix remains the behavioral contract.

---

## 30. Legacy class disposition

### Spring/application layer — DELETE after replacement

```text
JCmdArgsApplication
config.ExternalServiceConfig
API_connector.service.CommandLineService
API_connector.service.CommandLineServiceImpl
```

The new project is a library, not a Spring application.

### Legacy runtime parser — DELETE after parity

```text
module.cmd_line_parser.model.*
module.cmd_line_parser.performActions.*
module.cmd_line_parser.service.CommandLineParametersServiceImpl2
module.cmd_line_parser.service.CommandLineParserService
module.cmd_line_parser.service.impl.CommandLineParserServiceImpl
```

Replacement:

```text
model.*
runtime.*
api.ParseResult
```

Application actions remain outside parser core.

### Commons package — DELETE

```text
module.commons.exception.CmdArgsLineException
module.commons.global.StringUtils
module.commons.global.SystemEnvironmentVariable
module.commons.global.Utils
module.commons.service.FileIOService
module.commons.service.impl.FileIOServiceImpl
module.commons.service.model.PathType
module.commons.service.model.Tree
module.commons.service.model.TreeProperties
```

Do not create a new generic `commons` or `utils` package.

### Legacy definition parser — DELETE after parity

```text
module.definitions_file_parser.exception.*
module.definitions_file_parser.model.*
module.definitions_file_parser.service.*
module.definitions_file_parser.service.impl.*
```

Replacement:

```text
jca.*
model.*
diagnostic.*
```

`RuleServiceImpl` is a source of recovered rules, not a class to port.

---

## 31. Legacy-to-new conceptual mapping

| Legacy | Replacement |
|---|---|
| `DefinitionOption` | `OptionSpec` |
| `DefinitionNonOption` | `CommandSpec` / `ArgumentSpec` |
| `DefinitionType` | semantic types / `ArgumentCategory` |
| `RuleServiceImpl` | `JcaSemanticAnalyzer` |
| `CmdLineDefinitionParserServiceImpl` | `JcaCompiler` + `JcaParser` |
| `CommandLineParserServiceImpl` | `CliParser` |
| `CmdLineParamInfo` | `ParsedOption` / named arguments |
| action performer classes | consuming application |
| generated `.help` files | `Documentation` + renderers |
| logging/display services | `Diagnostic` |
| Spring DI state | constructors/local invocation state |
| `SystemEnvironmentVariable` | removed |
| `arguments_number` object | compile-time consistency assertion |

---

## 32. Implementation milestones

### A — Branch and design baseline

- create `rewrite/jca-1.0-java21-core`;
- add `/docs/future-roadmap.md`;
- add formal JCA specification;
- add test matrix;
- add this architecture document;
- do not rewrite production source yet.

### B — Framework-free build baseline

- plain Maven Java 21;
- remove Spring dependencies;
- remove `logs-indentation`;
- remove Lombok;
- configure JUnit 5.

This must be coordinated with removal/isolation of framework-dependent legacy classes so the project remains buildable.

### C — Semantic model

Implement:

```text
diagnostic.*
model.*
api.CompilationResult
```

### D — JCA compiler

Implement:

```text
JcaSource
JcaCompiler
jca.internal.*
```

Pass all DSL/semantic tests including:

```text
arguments_number
documentation metadata
order policy
atomic aliases
```

### E — Runtime parser

Implement:

```text
CliParser
ParsedCommandLine
ParsedOption
runtime.internal.*
api.ParseResult
```

Pass runtime and end-to-end tests.

### F — Renderers

Implement:

```text
HelpRenderer
ManualRenderer
UsageRenderer
```

No filesystem writes.

### G — Legacy removal

Remove legacy packages/resources only when the new implementation passes the behavioral contract.

Repository search should then find zero production references to:

```text
org.springframework
logs-indentation
lombok
module.cmd_line_parser
module.definitions_file_parser
module.commons
```

### H — Public documentation/release preparation

- rewrite README;
- canonical examples;
- public Java API documentation;
- diagnostics documentation;
- pre-1.0 release.

---

## 33. Commit strategy

Suggested narrow commits:

```text
docs: add JCA 1.0 design documents
build: establish framework-free Java 21 baseline
feat: add immutable CLI semantic model
feat: add structured diagnostics
feat: add JCA compiler
feat: validate JCA documentation metadata
feat: add runtime CLI parser
feat: add help and manual renderers
refactor: remove legacy parser implementation
docs: update JCmdArgs usage and examples
```

Do not commit the rewrite as one monolithic change.

---

## 34. Architecture decisions explicitly rejected

### No Spring dependency injection

Objects are ordinary Java objects.

### No interface + implementation pair by default

Do not recreate patterns such as:

```text
RuleService
RuleServiceImpl
```

when only one implementation exists and no meaningful abstraction is needed.

### No generic utility package

Helpers remain close to the code that owns them.

### No action execution in parser core

Parsing reports what the user requested. The application decides what to do.

### No generated help files during parsing

Render help/manual as text/data on demand.

### No hidden global state

All mutable compile/parse state is invocation-local.

### No raw JCA markers in runtime model

`#`, `!`, `+`, `?`, braces and raw syntax stop at the compiler boundary.

---

## 35. Architectural invariants

1. JCA compilation is stateless.
2. Runtime parsing is stateless between invocations.
3. `CliSpec` is immutable.
4. Successful `CliSpec` contains no unresolved documentation targets.
5. Aliases preserve exact `-` / `--` spelling.
6. `-lf` is atomic when explicitly declared.
7. `arguments_number` is compile-time metadata.
8. Runtime parser never reads raw `.jca`.
9. Renderers never parse raw `.jca`.
10. Core never executes application actions.
11. Core never writes help files automatically.
12. Core returns structured diagnostics instead of logging validation errors.
13. Invalid user input is not exceptional control flow.
14. Runtime dependency target is zero.
15. Java baseline is 21.

---

## 36. Final target usage

Direct API:

```java
var compilation = new JcaCompiler().compile(
        JcaSource.from(Path.of("application.jca"))
);

if (!compilation.success()) {
    // application decides how diagnostics are displayed
}

CliSpec spec = compilation.cliSpec().orElseThrow();

var result = new CliParser().parse(spec, args);

if (!result.success()) {
    // application decides how runtime diagnostics are displayed
}

ParsedCommandLine commandLine = result.commandLine().orElseThrow();
```

Convenience facade:

```java
var compilation = JCmdArgs.compile(Path.of("application.jca"));
var result = JCmdArgs.parse(compilation.cliSpec().orElseThrow(), args);
```

Documentation:

```java
String help = new HelpRenderer().render(spec);
String manual = new ManualRenderer().render(spec);
```

The consuming application owns:

- output;
- logging;
- exit codes;
- action execution;
- application configuration;
- dependency injection.

JCmdArgs owns:

- the CLI contract;
- contract validation;
- runtime interpretation;
- derived documentation.
