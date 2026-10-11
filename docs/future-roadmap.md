# JCmdArgs — Future Directions and Product Roadmap

**Status:** Living design document  
**Scope:** Ideas intentionally outside the initial JCA 1.0 baseline, plus longer-term product opportunities  
**Java reference implementation baseline:** Java 21

---

## 1. Purpose

This document preserves future ideas for JCmdArgs without forcing them into the first stable implementation.

JCA 1.0 should remain focused, coherent, testable, and small. Features listed here are **not commitments for 1.0** unless they are later promoted into the formal JCA specification.

The central product direction is:

> **JCmdArgs is a schema-first command-line interface engine. A `.jca` file defines the CLI contract — syntax, validation, defaults, aliases, ordering and documentation — independently of application code.**

The `.jca` contract should remain the single source of truth from which runtime behavior and documentation can be derived.

---

## 2. Core product differentiator

JCmdArgs should not position itself primarily as “another Java argument parser”.

Its stronger positioning is:

> **Define a complete command-line contract once, validate it independently, and derive consistent runtime parsing and documentation from it.**

The important properties are:

- external declarative contract;
- independent from Java annotations;
- machine-validatable before application startup;
- human-readable;
- documentation stored alongside syntax and validation rules;
- deterministic compilation into an immutable CLI model;
- runtime parser as one consumer of that model, not the entire product;
- zero mandatory runtime framework dependencies in the Java core.

Potential long-term terminology:

- CLI schema;
- CLI contract;
- JCA compiler;
- JCA validator/linter;
- CLI model compiler;
- CLI contract engine.

---

## 3. JCA as a language-neutral contract

The first reference implementation is Java 21, but the `.jca` format should avoid unnecessary Java-specific semantics.

Long-term possibility:

```text
application.jca
        |
        v
      CliSpec
     /   |   \
 Java   Go   Python ...
```

Possible future runtimes could consume the same CLI contract in other languages.

This is not a near-term implementation target. It is an architectural constraint worth preserving:

> Do not design the JCA language in a way that can only be interpreted by Java reflection or Java types.

---

## 4. Standalone JCA compiler / validator

A future `jcmdargs` command-line tool could validate `.jca` files independently of any consuming application.

Example:

```text
jcmdargs validate application.jca
```

Possible output:

```text
ERROR JCA203
arguments_number declares 3 positional arguments,
but only 2 argument definitions were found.

ERROR JCA501
Documentation references option '--debgu',
but no such option exists.

WARNING JCA504
Option '--output' has no summary.
```

Potential commands:

```text
jcmdargs validate application.jca
jcmdargs inspect application.jca
jcmdargs help application.jca
jcmdargs render-man application.jca
jcmdargs render-markdown application.jca
```

This would turn JCmdArgs into more than a runtime library: it would also be a CLI contract development tool.

---

## 5. Publication / strict validation mode

Normal validation should distinguish structural errors from documentation quality.

A future publication mode could enforce stronger quality gates:

```text
jcmdargs validate --publication application.jca
```

Potential checks:

- every public command has a summary;
- every option has documentation;
- every positional argument has documentation;
- descriptions are not empty;
- aliases are documented consistently;
- defaults are valid;
- examples reference existing commands/options;
- no deprecated definitions remain;
- no undocumented public surface exists.

The normal compiler may treat missing documentation as a warning, while publication mode could promote selected warnings to errors.

---

## 6. Typed values

JCA 1.0 can initially operate on strings, but a mature CLI system benefits from declarative types.

Possible future syntax — exact grammar to be designed later:

```properties
option=+{--threads,-t}=# type=int
option={--timeout,-to}=# type=duration
argument=source=# type=path
```

Candidate built-in types:

- string;
- boolean;
- integer;
- long;
- decimal;
- path;
- file;
- directory;
- URI;
- URL;
- duration;
- date/time;
- enum.

The type system should remain implementation-neutral where possible.

Potential result API:

```java
result.option("--threads", Integer.class);
result.argument("source", Path.class);
```

or a typed semantic model that avoids runtime casts.

---

## 7. Declarative constraints

Future JCA versions could validate values without application code.

Examples of possible constraints:

```text
minimum / maximum
regex
minimum length
maximum length
file must exist
directory must exist
allowed extension
numeric range
```

Illustrative only:

```properties
option=+{--threads,-t}=# type=int min=1 max=128
argument=config=# type=file exists=true
```

The grammar must remain readable; JCA should not become an unreadable general-purpose programming language.

---

## 8. Required options

Required options are useful when a value must be supplied by name rather than position.

Current design candidate:

```properties
option=+{--config,-c}=#
```

Long-term considerations:

- interactions with standalone options such as `--help`;
- error ordering;
- whether required options can have defaults;
- whether requirement can be conditional on a command/subcommand.

---

## 9. Optional positional arguments without defaults

A future/approved syntax may represent a positional argument that may be omitted and has no default.

Design candidate:

```properties
argument=destination=?
```

Important questions:

- ordering restrictions;
- whether a required positional argument may follow an optional one;
- ambiguity resolution;
- interaction with variadic arguments.

The compiler should reject grammars that are ambiguous.

---

## 10. Variadic positional arguments

Potential use cases:

```text
cp file1 file2 file3 destination
tool input1 input2 input3
```

Possible model:

```text
ArgumentSpec
  minOccurrences
  maxOccurrences
```

Potential future syntax must be designed explicitly rather than inferred from repeated `argument=` lines.

The parser should be able to detect ambiguous combinations of optional and variadic arguments during JCA compilation.

---

## 11. Repeatable options

Examples:

```text
-I include1 -I include2
--tag one --tag two
```

Possible future semantic model:

```text
repeatable = true
minOccurrences = 0
maxOccurrences = unlimited
```

JCA 1.0 should treat repeated scalar options as an error unless repeatability is explicitly introduced later.

---

## 12. Short-option clustering

JCmdArgs explicitly supports atomic multi-character short aliases:

```properties
option={--long-form,-lf}
```

Therefore:

```text
-lf
```

means the explicitly declared alias `-lf`, not automatically `-l -f`.

Traditional Unix-style clustering such as:

```text
-abc
```

meaning:

```text
-a -b -c
```

may be considered later, but only through explicit grammar rules that avoid conflict with atomic aliases like `-lf`.

No automatic clustering should be inferred in JCA 1.0.

---

## 13. Commands and nested subcommands

The initial model may keep one command grammar per JCA definition.

A future version could support command trees:

```text
app
├── clone
├── init
├── status
└── config
    ├── get
    └── set
```

Important design questions:

- one `.jca` file for the entire tree vs. imports/includes;
- inheritance of global options;
- local options;
- command-specific arguments;
- documentation hierarchy;
- command aliases;
- command-level ordering rules.

The model should be designed so this can be added without breaking simple JCA files.

---

## 14. Mutually exclusive option groups

Standalone options such as `--help` are not the same as general mutually exclusive groups.

Future examples:

```text
--json OR --xml
--quiet OR --verbose
```

Possible capabilities:

- exactly one required;
- zero or one allowed;
- at least one required;
- group-level documentation.

This should be modeled explicitly rather than overloading `!`.

---

## 15. Conditional rules

Potential advanced scenarios:

```text
--username requires --password
--output required when --export is present
--format valid only for command export
```

Conditional validation can become complex quickly.

If introduced, the syntax should remain declarative and analyzable at compile time. JCA should avoid turning into a scripting language.

---

## 16. Environment-variable fallback

Possible future behavior:

```text
CLI value
  >
environment variable
  >
JCA default
```

Illustrative idea:

```properties
option={--token,-t}=# env=APP_TOKEN
```

Security consideration:

- secret values should never be automatically rendered in diagnostics or help output.

---

## 17. Configuration-file fallback

A future integration could merge values from:

1. command line;
2. environment variables;
3. application configuration;
4. JCA defaults.

Precedence rules must be explicit.

This feature should be modular; the core parser should not acquire framework or file-format dependencies.

---

## 18. Response files

Potential syntax:

```text
app @args.txt
```

where `args.txt` contains CLI tokens.

Useful for very long invocations and automated tooling.

Security and quoting rules must be defined before implementation.

---

## 19. Shell completion generation

Because the JCA model already knows:

- commands;
- aliases;
- allowed enum values;
- positional structure;
- documentation;

it could generate shell completion definitions.

Possible targets:

- Bash;
- Zsh;
- Fish;
- PowerShell.

This is a strong example of the value of treating `.jca` as a schema rather than merely parser configuration.

---

## 20. Help and manual renderers

The initial renderer should remain simple.

Long-term renderers could include:

```text
terminal help
detailed terminal manual
Markdown
man page
HTML
```

All should consume `CliSpec`; none should re-parse the raw `.jca`.

Structural information such as:

- required;
- default;
- implicit value;
- aliases;
- allowed values;

should be rendered automatically.

Authors should not need to duplicate these facts in prose.

---

## 21. Usage generation

The model should eventually derive canonical usage lines automatically:

```text
Usage:
  app clone [options] <source> [destination]
```

Potential advanced features:

- multiple valid usage forms;
- optional groups;
- mutually exclusive groups;
- variadic arguments;
- subcommands.

Usage text should be generated from the grammar whenever practical rather than maintained manually.

---

## 22. Examples in JCA documentation

Future JCA documentation may support named examples.

Illustrative only:

```properties
doc.example.basic="clone https://example/repo.git"
doc.example.debug="clone --debug=enable https://example/repo.git"
```

Potential validation:

- referenced command exists;
- referenced options exist;
- example can optionally be parsed against the same `CliSpec`.

The strongest version of this feature would make documentation examples executable tests.

---

## 23. Documentation consistency validation

Because documentation belongs to the same contract, JCmdArgs can detect:

- documentation for unknown targets;
- duplicated documentation fields;
- missing summaries;
- stale examples;
- descriptions for aliases that do not exist;
- references to invalid defaults.

This is a meaningful advantage of the schema-first design.

---

## 24. Localization

Potential future localization model:

```text
application.jca
application.en.jca-doc
application.ro.jca-doc
application.fr.jca-doc
```

or another mechanism that keeps grammar separate from translated prose.

The core syntax and semantic contract should not need to be duplicated for each language.

---

## 25. Machine-readable compiled representation

A future JCA compiler could emit a stable machine-readable model such as JSON.

Example:

```text
jcmdargs compile application.jca --format json
```

Possible uses:

- IDE tooling;
- documentation websites;
- validation pipelines;
- other language runtimes;
- integrations that do not want to parse JCA directly.

The compiled representation would need versioning.

---

## 26. IDE support

Potential tooling:

- syntax highlighting;
- autocomplete;
- diagnostic underlining;
- alias collision detection;
- documentation target completion;
- preview of generated help;
- navigation from documentation to option definition.

Potential targets:

- IntelliJ IDEA;
- VS Code.

A machine-readable grammar/schema and stable diagnostic codes would make this feasible.

---

## 27. Language server

If JCA adoption grows, a small Language Server Protocol implementation could provide editor support across IDEs.

Capabilities:

- parse and validate while typing;
- completion;
- hover documentation;
- go-to-definition;
- rename canonical targets safely;
- quick fixes.

This is a long-term ecosystem idea, not a core requirement.

---

## 28. Build-system integration

Possible optional plugins:

```text
Maven plugin
Gradle plugin
```

Example build-time validation:

```text
validateJca
generateCliDocs
```

A failed JCA contract could then fail the build before packaging.

The core library itself should remain build-tool agnostic.

---

## 29. CI quality gates

Potential CI workflow:

```text
compile
test
jcmdargs validate --publication
generate documentation
```

This makes CLI compatibility and documentation quality testable artifacts.

Future tooling might also compare two JCA contracts to detect breaking changes.

---

## 30. CLI contract compatibility / semantic diff

A potentially strong future feature:

```text
jcmdargs diff old.jca new.jca
```

Possible classifications:

```text
BREAKING
--output removed

BREAKING
argument 'source' changed from optional to required

NON-BREAKING
alias '-o' added

DOCUMENTATION
summary changed
```

This could enable semantic-versioning checks for command-line interfaces.

It fits particularly well with the concept of `.jca` as an external public contract.

---

## 31. Versioning the JCA language

The JCA format itself will eventually evolve.

A future explicit language version may be necessary:

```properties
jca_version=1
```

or equivalent.

Goals:

- deterministic parser behavior;
- backwards compatibility;
- controlled deprecation;
- migration tooling.

This should be introduced when it becomes necessary, not prematurely.

---

## 32. Includes / composition

For large applications, one monolithic JCA file may become inconvenient.

Potential future composition:

```text
main.jca
commands/clone.jca
commands/status.jca
common/global-options.jca
```

Questions:

- import syntax;
- cycle detection;
- alias collision rules;
- documentation merge semantics;
- source locations in diagnostics.

This should only be introduced after the single-file model is mature.

---

## 33. Reusable fragments

Beyond raw includes, future JCA versions could support reusable option groups.

Example use cases:

- common logging options;
- network options;
- authentication options.

Reuse must not compromise readability or make the effective CLI contract difficult to understand.

---

## 34. Deprecation metadata

Public CLIs evolve.

Possible future support:

```text
deprecated option
replacement option
deprecated command
removal version
```

Generated help could show deprecation notices, and diagnostics could warn when deprecated syntax is used.

---

## 35. Hidden/internal options

Some tools need options that exist but should not appear in normal help.

Potential future metadata:

```text
visibility = hidden
```

Publication validation should distinguish intentional hidden elements from accidentally undocumented elements.

---

## 36. Sensitive options

Examples:

```text
--password
--token
```

Potential semantic metadata:

```text
sensitive = true
```

Effects:

- never echo value in diagnostics;
- mask values in debug representations;
- omit default values from help if necessary.

---

## 37. Parser introspection API

Applications may need to inspect the compiled contract.

Potential API:

```java
CliSpec spec = compiler.compile(...);

spec.options();
spec.arguments();
spec.command();
```

This should expose immutable semantic information and not parser internals.

---

## 38. Programmatic CliSpec builder

Although `.jca` is the primary product differentiator, a programmatic builder may eventually be useful.

Potential uses:

- testing;
- dynamically generated CLIs;
- integration with other configuration formats.

Important:

> The programmatic API should build the same `CliSpec` model as the JCA compiler.

There should not be two independent runtime engines.

---

## 39. Import/export from other ecosystems

Possible long-term interoperability:

- generate JCA from another CLI description;
- export JCA to documentation formats;
- bridge to completion/man-page tooling.

This should only be considered where semantics map cleanly.

---

## 40. Optional Spring integration

The core JCmdArgs library should remain independent of Spring.

A future optional module could provide:

```text
jcmdargs-spring
```

Potential functionality:

- bean creation;
- configuration binding;
- integration with application startup.

Spring must remain an adapter, never a requirement of the core.

---

## 41. Optional logging integrations

The core should return diagnostics as data and should not require a logging framework.

Future adapters/examples may integrate with:

- JUL;
- SLF4J;
- Log4j;
- `logs-indentation`.

`logs-indentation` should remain an independent project. JCmdArgs core must not depend on it.

---

## 42. Zero-runtime-dependency core

A strategic goal worth preserving:

```text
jcmdargs-core
runtime dependencies = 0
```

Java 21 provides enough language and standard-library functionality for the core parser/compiler.

Optional integrations may depend on external libraries, but they should be separate modules.

---

## 43. Immutable compiled model

The compiled semantic representation should remain immutable.

Conceptually:

```text
JCA source
   |
   v
JcaCompiler
   |
   v
CliSpec
  |  \
  |   \--> documentation renderers
  |
  +------> runtime parser
```

Benefits:

- thread safety;
- deterministic behavior;
- simple caching;
- parallel parsing;
- no legacy-style mutable singleton state.

---

## 44. Compilation caching

For applications repeatedly using the same contract, a compiled `CliSpec` may be cached.

This should be an application-level optimization or optional utility.

The compiler itself should remain stateless.

---

## 45. Source-aware diagnostics

Future diagnostics can retain precise source locations:

```text
file
line
column
definition kind
target
```

Example:

```text
application.jca:17:8
JCA103 OPTION_ALIAS_COLLISION
Alias '-o' is already used by '--output'.
```

This would significantly improve editor and CI integration.

---

## 46. Error recovery

An IDE-oriented compiler may need to continue parsing after a syntax error so it can report multiple diagnostics in one pass.

Runtime loading may choose a stricter fail-fast or collect-all-errors mode.

Both should share the same diagnostic model.

---

## 47. Stable diagnostic codes

Diagnostic codes should become part of the public tooling contract.

Examples:

```text
JCA203 ARGUMENT_COUNT_MISMATCH
JCA501 UNKNOWN_DOCUMENTATION_TARGET
CLI001 UNKNOWN_OPTION
```

Benefits:

- reliable tests;
- CI filtering;
- IDE tooling;
- translations;
- automation.

Message prose may evolve while codes remain stable.

---

## 48. Contract tests generated from JCA

A mature JCmdArgs ecosystem could generate baseline tests from a contract.

Examples:

- every alias resolves;
- every default is valid;
- standalone options bypass required arguments;
- help can render;
- enum values are accepted;
- invalid enum values are rejected.

Generated tests should complement, not replace, application-specific tests.

---

## 49. Documentation examples as executable specifications

A particularly strong future direction is validating examples embedded in documentation against the same parser.

If a `.jca` example says:

```text
clone --debug=enable source destination
```

the documentation build could ensure that this command is still valid.

That would reduce stale CLI documentation.

---

## 50. Semantic contract diff and release policy

Because CLI behavior is encoded declaratively, releases could automatically detect public-interface changes.

Potential release checks:

- removed alias = breaking;
- required argument added = breaking;
- optional alias added = non-breaking;
- documentation-only change = non-breaking;
- default changed = potentially breaking.

This could eventually support semantic-versioning recommendations.

---

## 51. Potential repository structure

Long-term, only if needed:

```text
jcmdargs-core
jcmdargs-cli
jcmdargs-renderer-markdown
jcmdargs-renderer-man
jcmdargs-maven-plugin
jcmdargs-gradle-plugin
jcmdargs-spring
```

Do not create modules simply because they are imaginable.

Start with the smallest structure that keeps the Java 21 core clean.

---

## 52. Project positioning

Avoid weak positioning such as:

> Java command-line argument parser.

Prefer:

> **JCmdArgs is a schema-first command-line interface engine. Define syntax, validation and documentation once in a `.jca` contract and compile it into a consistent CLI model.**

Potential short descriptions:

> Declarative CLI contracts for Java.

> Define once. Validate once. Parse and document consistently.

> CLI syntax, validation and documentation from one source of truth.

These are positioning candidates, not final branding.

---

## 53. What JCmdArgs should not become

To protect the design, avoid these traps:

### 53.1 A second programming language

JCA should remain declarative. Complex procedural logic belongs in the consuming application.

### 53.2 A framework requirement

Spring, logging libraries, dependency injection containers and build tools should remain optional integrations.

### 53.3 A configuration dumping ground

Do not move unrelated application configuration into `.jca`.

`.jca` describes the command-line contract.

### 53.4 A clone of picocli with different syntax

Competing only by duplicating every feature of a mature parser would dilute the project's strongest idea.

The advantage should remain the external, structured, validated contract.

### 53.5 Documentation duplicated from semantics

If JCmdArgs already knows an option is required, its aliases, defaults or accepted values, the author should not have to repeat them in prose.

Renderers should derive structural facts automatically.

---

## 54. Decision filter for future features

Before promoting an idea into the JCA language, ask:

1. Does this describe the public command-line contract?
2. Can it be validated deterministically?
3. Can it be represented declaratively?
4. Does it preserve readability?
5. Does it work without Java-specific reflection?
6. Does it strengthen the single-source-of-truth model?
7. Can it be added without making simple `.jca` files harder to understand?

If several answers are “no”, the feature probably belongs in application code or an adapter rather than JCA.

---

## 55. Current strategic boundary

The immediate rewrite should focus on the stable JCA 1.0 contract and the Java 21 reference implementation.

This roadmap exists specifically so that attractive future ideas do **not** cause scope creep during the rewrite.

The architecture should keep these possibilities open where reasonably inexpensive, but the first implementation should optimize for:

- correctness;
- clarity;
- deterministic validation;
- excellent diagnostics;
- immutable models;
- zero mandatory runtime dependencies;
- strong tests;
- coherent documentation;
- a small, understandable public API.

