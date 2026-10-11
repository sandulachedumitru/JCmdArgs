# JCmdArgs — JCA 1.0 Test Matrix

**Status:** Draft 0.2  
**Date:** 2026-10-11  
**Java baseline:** Java 21  
**Purpose:** Executable behavioral contract for the JCA 1.0 rewrite.

## 1. Test layers

The suite validates four layers:

1. DSL lexical/syntactic parsing;
2. semantic compilation and validation;
3. runtime CLI parsing;
4. end-to-end JCA -> `CliSpec` -> `ParseResult` / rendered help.

Tests must be deterministic, stateless and independent of network, global environment, console output and filesystem mutation unless explicitly testing an I/O adapter.

## 2. Linux-style alias tests

### ALIAS-001 — Long option retained exactly

JCA:

```properties
option={--long-form}
```

CLI:

```text
--long-form
```

Expected: option recognized.

### ALIAS-002 — Multi-character short alias is atomic

JCA:

```properties
option={--long-form,-lf}
```

CLI:

```text
-lf
```

Expected: same logical option as `--long-form`.

### ALIAS-003 — No automatic clustering

JCA:

```properties
option={--long,-l}
option={--force,-f}
```

CLI:

```text
-lf
```

Expected:

```text
CLI001 UNKNOWN_OPTION
```

`-lf` must not be expanded to `-l -f`.

### ALIAS-004 — Alias collision

```properties
option={--long-form,-lf}
option={--log-file,-lf}
```

Expected:

```text
JCA103 OPTION_ALIAS_COLLISION
```

## 3. `arguments_number` tests

### COUNT-001 — Absent

```properties
argument=a=#
argument=b=#
```

Expected: valid, derived count = 2.

### COUNT-002 — Exact match

```properties
arguments_number=2
argument=a=#
argument=b=#
```

Expected: valid.

### COUNT-003 — Forgotten argument

```properties
arguments_number=3
argument=a=#
argument=b=#
```

Expected:

```text
JCA203 ARGUMENT_COUNT_MISMATCH
```

### COUNT-004 — Extra argument

```properties
arguments_number=2
argument=a=#
argument=b=#
argument=c=#
```

Expected:

```text
JCA203 ARGUMENT_COUNT_MISMATCH
```

### COUNT-005 — Explicit zero

```properties
arguments_number=0
command=status
```

Expected: valid.

### COUNT-006 — Invalid numeric value

```properties
arguments_number=-1
```

and

```properties
arguments_number=three
```

Expected:

```text
JCA205 INVALID_ARGUMENT_COUNT
```

## 4. Required/optional positional argument tests

### ARG-001 — Required argument

```properties
argument=source=#
```

No CLI value =>

```text
CLI101 REQUIRED_ARGUMENT_MISSING
```

### ARG-002 — Optional argument without default

```properties
argument=destination=?
```

No CLI value => successful parse; `destination` is absent.

### ARG-003 — Optional argument with default

```properties
argument=destination=repository
```

No CLI value => `destination = repository`.

### ARG-004 — Literal question mark

```properties
argument=value="?"
```

Expected default value is literal `?`, not optional marker.

### ARG-005 — Required after optional is invalid

```properties
argument=destination=?
argument=source=#
```

Expected semantic error:

```text
JCA207 REQUIRED_ARGUMENT_AFTER_OPTIONAL
```

### ARG-006 — Named lookup

```properties
argument=source=#
```

CLI:

```text
repo.git
```

Expected semantic API result:

```java
result.argument("source") == "repo.git"
```

## 5. Required option tests

### REQOPT-001 — Required free-value option present

```properties
option=+{--config,-c}=#
```

CLI:

```text
--config app.conf
```

Expected: valid.

### REQOPT-002 — Required option absent

Same JCA, empty CLI.

Expected:

```text
CLI005 REQUIRED_OPTION_MISSING
```

### REQOPT-003 — Required option bypassed by standalone help

```properties
option=!{--help,-h}
option=+{--config,-c}=#
```

CLI:

```text
--help
```

Expected: valid standalone invocation; no `CLI005`.

### REQOPT-004 — `!` and `+` cannot be combined

Any syntax attempting to mark one option both standalone and required must fail semantic compilation.

Expected:

```text
JCA108 INCOMPATIBLE_OPTION_MODIFIERS
```

## 6. Option value semantics

### VALUE-001 — Flag

```properties
option={--verbose,-v}
```

`--verbose` => present.

`--verbose=true` =>

```text
CLI002 OPTION_DOES_NOT_ACCEPT_VALUE
```

### VALUE-002 — Enum with implicit first value

```properties
option={--debug,-d}={disable,enable}
```

`--debug` => `disable`.

`--debug=enable` => `enable`.

`--debug=other` =>

```text
CLI003 INVALID_OPTION_VALUE
```

### VALUE-003 — No separated override for implicit enum

Same JCA.

CLI:

```text
--debug enable
```

Expected: `--debug` resolves to `disable`; `enable` remains a separate semantic token (positional if allowed, otherwise causes the appropriate positional/unknown-input error).

### VALUE-004 — Required enum

```properties
option={--mode,-m}=#{fast,slow}
```

`--mode=fast` => valid.

`--mode fast` => valid.

`--mode` =>

```text
CLI004 OPTION_VALUE_REQUIRED
```

`--mode=invalid` =>

```text
CLI003 INVALID_OPTION_VALUE
```

### VALUE-005 — Free required value

```properties
option={--output,-o}=#
```

Both are valid:

```text
--output=file.txt
--output file.txt
```

Bare `--output` =>

```text
CLI004 OPTION_VALUE_REQUIRED
```

### VALUE-006 — Free implicit value

```properties
option={--print-to,-pt}={Logs.txt,#}
```

`--print-to` => `Logs.txt`.

`--print-to=Build.log` => `Build.log`.

Separated `--print-to Build.log` does not consume `Build.log` as the option value.

### VALUE-007 — Option absent does not inject implicit value

```properties
option={--debug,-d}={disable,enable}
```

Empty CLI => option is absent; result must not claim that `debug=disable` was supplied/effective merely because `disable` is the implicit value.

## 7. Standalone option tests

### STAND-001

```properties
option=!{--help,-h}
argument=source=#
```

CLI `--help` => valid, required argument bypassed.

### STAND-002

CLI `--help value` =>

```text
CLI201 STANDALONE_OPTION_CONFLICT
```

### STAND-003

```properties
option=!{--help,-h}
option={--verbose,-v}
```

CLI `--help --verbose` =>

```text
CLI201 STANDALONE_OPTION_CONFLICT
```

## 8. Documentation tests

### DOC-001 — Root summary

```properties
doc.summary="Repository clone utility."
```

Expected: stored in `CliSpec.documentation.summary`.

### DOC-002 — Multiline root description

```properties
doc.description="""
Line one.
Line two.
"""
```

Expected: complete multiline text preserved according to documented newline normalization.

### DOC-003 — Option documentation targets exact long alias

```properties
option={--long-form,-lf}
doc.option.--long-form.summary="Use the long output form."
```

Expected: documentation attached to the logical option represented by both aliases.

### DOC-004 — Short alias may resolve same logical option

```properties
option={--long-form,-lf}
doc.option.-lf.summary="Use the long output form."
```

Expected: documentation attaches to the same option.

### DOC-005 — Duplicate documentation through aliases

```properties
option={--long-form,-lf}
doc.option.--long-form.summary="Long form."
doc.option.-lf.summary="Another summary."
```

Expected:

```text
JCA502 DUPLICATE_DOCUMENTATION_FIELD
```

### DOC-006 — Unknown option target

```properties
doc.option.--missing.summary="Missing option."
```

Expected:

```text
JCA501 UNKNOWN_DOCUMENTATION_TARGET
```

### DOC-007 — Unknown argument target

```properties
doc.argument.missing.summary="Missing argument."
```

Expected:

```text
JCA501 UNKNOWN_DOCUMENTATION_TARGET
```

### DOC-008 — Unknown command target

```properties
doc.command.missing.summary="Missing command."
```

Expected:

```text
JCA501 UNKNOWN_DOCUMENTATION_TARGET
```

### DOC-009 — Documentation may precede definition

```properties
doc.option.--long-form.summary="Use the long output form."
option={--long-form,-lf}
```

Expected: valid after semantic target resolution.

### DOC-010 — Derived technical information is not duplicated in source

Given:

```properties
option={--debug,-d}={disable,enable}
doc.option.--debug.summary="Control debug output."
```

help renderer output must derive aliases, enum values and implicit value from `OptionSpec`, while using the authored summary for prose.

## 9. Help rendering tests

### HELP-001 — Linux aliases rendered

```properties
option={--long-form,-lf}
doc.option.--long-form.summary="Use the long output form."
```

Rendered help includes both `-lf` and `--long-form`.

### HELP-002 — Required option rendered as required

```properties
option=+{--config,-c}=#
```

Help output identifies the option as required without requiring the author to repeat that fact in documentation text.

### HELP-003 — Positional default derived

```properties
argument=destination=repository
doc.argument.destination.summary="Destination directory."
```

Help renderer identifies `repository` as the default.

### HELP-004 — Optional positional no-default derived

```properties
argument=destination=?
```

Help renderer represents the argument as optional without inventing a default.

## 10. End-of-options marker

```properties
option={--long-form,-lf}
argument=value=#
```

CLI:

```text
-- --long-form
```

Expected: positional `value = --long-form`; option is absent.

## 11. Order policy tests

With:

```properties
allowed_arguments_order={command,option,argument}
command=clone
option={--long-form,-lf}
argument=source=#
```

`clone --long-form repo` => valid.

`clone repo --long-form` =>

```text
CLI301 ARGUMENT_ORDER_VIOLATION
```

## 12. State isolation tests

- sequential JCA compilations do not share definitions;
- failed compilation cannot poison later compilation;
- concurrent compilations do not cross-contaminate state;
- runtime parsers do not mutate `CliSpec`.

## 13. Side-effect tests

Compilation and runtime parsing must:

- create no files/directories;
- modify no files;
- emit nothing to `System.out`;
- emit nothing to `System.err`;
- require no logger initialization.

## 14. End-to-end baseline

JCA:

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
doc.command.clone.summary="Clone a repository."
doc.option.--help.summary="Show help and exit."
doc.option.--debug.summary="Control debug output."
doc.option.--long-form.summary="Use the long output form."
doc.option.--config.summary="Configuration file to use."
doc.option.--print-to.summary="Write output to a file."
doc.argument.source.summary="Source repository or directory."
doc.argument.destination.summary="Destination directory."
```

CLI:

```text
clone --config app.conf --debug=enable -lf --print-to=build.log src dst
```

Expected semantic result:

```text
command      = clone
config       = app.conf
debug        = enable
long-form    = present
print-to     = build.log
source       = src
destination  = dst
```

No diagnostic.

Standalone CLI:

```text
clone --help
```

Expected: valid even though required option `--config` and required argument `source` are absent.

## 15. Initial diagnostic registry additions

### JCA

| Code | Meaning |
|---|---|
| JCA101 | Option alias required |
| JCA103 | Option alias collision |
| JCA108 | Incompatible option modifiers |
| JCA203 | Argument count mismatch |
| JCA205 | Invalid argument count |
| JCA207 | Required argument after optional |
| JCA501 | Unknown documentation target |
| JCA502 | Duplicate documentation field |
| JCA503 | Invalid documentation field |
| JCA504 | Missing documentation (future publication warning) |

### Runtime

| Code | Meaning |
|---|---|
| CLI001 | Unknown option |
| CLI002 | Option does not accept value |
| CLI003 | Invalid option value |
| CLI004 | Option value required |
| CLI005 | Required option missing |
| CLI101 | Required positional argument missing |
| CLI102 | Too many positional arguments |
| CLI201 | Standalone option conflict |
| CLI301 | Argument order violation |
| CLI401 | Option repeated |

## 16. Definition of Done for the first Java 21 core

The new core is eligible to replace the legacy parser only when:

- all accepted JCA 1.0 contract tests pass;
- Java baseline is 21;
- semantic models are immutable;
- compiler/parser components are stateless;
- aliases preserve `-` / `--` public syntax;
- multi-character short aliases such as `-lf` are atomic;
- JCA documentation resolves to semantic targets;
- help is derived from the same `CliSpec` used by runtime parsing;
- `arguments_number` catches declared/actual count mismatch;
- no Spring Boot dependency exists in core;
- no `logs-indentation` dependency exists in core;
- no core filesystem side effects occur;
- no core stdout/stderr side effects occur;
- structured diagnostics cover invalid JCA and invalid runtime input.

## 17. Deliberately deferred tests

No implementation tests should be written yet for:

- nested subcommands;
- multiple command branches;
- repeatable options;
- automatic short-option clustering;
- variadic positional arguments;
- typed application conversion;
- advanced value constraints;
- environment fallback;
- shell completion;
- localization.

These require separate language-design decisions after the 1.0 baseline is stable.
