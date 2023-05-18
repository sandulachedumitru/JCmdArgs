# JCmdArgs

## _Java Command Line Arguments Processor_

The engine that parses command-line arguments will look for the definition file that is defined in the
application.properties file.

### General form:

```
{option, command, arguments_number, argument} option=[[!]{--<optionLong>,-<optionShort>}[={}]] command=[<command>] argument=[<argument>] arguments_number=[<number>]
```

```
{option, command, arguments_number, argument}
option=[[!]{--<optionLong>,-<optionShort>}[={}]]
command=[<command>]
argument=[<argument>]
arguments_number=[<number>]
```

> **NOTE**: No space character will be allowed inside element

### Special characters and syntax description

| Symbol                     | Description                                    |
|----------------------------|------------------------------------------------|
| `#`                        | `mandatory user input`                         |
| `!`                        | `single option, other options are not allowed` |
| `[]`                       | `is optional`                                  |
| `{<value1>,<value2>, ...}` | `the list of supported values of the option`   |
| `{}`                       | `list of variable size`                        |
| `,`                        | `list values separator`                        |
| `<something>`              | `replace with real word`                       |
| `option=`                  | `defines an option`                            |
| `command=`                 | `defines a command`                            |
| `argument=`                | `defines an argument`                          |
| `arguments_number=`        | `the number of arguments allowed`              |

Example 1:

```
{option, command, arguments_number, argument}
option=!{--help,-h}
option={--debug,-d}={enable,disable}
option={--print-to,-pt}={Logs.txt,#}
option={--with-rootPath}={enable,disable}                           -> no short version option
command=clone
arguments_number=2
argument=https://github.com/sandulachedumitru/JCmdArgs.git
argument=#                                                          -> #: mandatory user input
argument=something -> will be ignored because arguments_number==2
```

Example 2:

```
$ git clone https://github.com/sandulachedumitru/JCmdArgs.git
{command, arguments_number, argument}
command=clone
arguments_number=1
argument=#
argument=something -> will be ignored because arguments_number==1
```

Example 3:

```
$ sudoku --debug=enable -pt SudokuInputFile.txt
$ sudoku --d=enable --print-to=SudokuLog.log SudokuInputFile.txt
$ sudoku --d=enable --print-to=SudokuLog.log
{option, arguments_number, argument}
option=!{--help,-h}
option={--debug,-d}={enable,disable}
option={--print-to,-pt}={Logs.txt,#}
arguments_number=1
argument=sudokufile.txt
```

### Options Form:

```
--option                                    -> option long name
--option=value
-o                                          -> option short name
-o=value
--option{(-something)xN}
--option-something1
--option-something1=value
--option-something1-something2
--option-something1-something2=value
...
```

### Options description, user guide (help); ex:

Every option user guide must have an `<option>.help` file with a name that matches the long option name.

Example:

```
--help,		-h		-> help.help
--debug,	-d		-> debug.help
--print-to,	-pt		-> print-to.help
```

### Options/Arguments special symbol

```
#       -> mandatory user input
!       -> single option, other options are not allowed
```

### Arguments form:

```
argument=<default value>    -> NOT mandatory; user input OR dafault value
argument=#                  -> mandatory; user input; no default value provided for argument
```

Example 1:

```
argument=sudokufile.txt		-> NOT mandatory (because it has default value); user input OR dafault value

$ sudoku --d=enable --print-to=SudokuLog.log SudokuInputFile.txt
- "SudokuInputFile.txt" argument overrides default "sudoku.txt" argument
$ sudoku --d=enable --print-to=SudokuLog.log
- argument has default value "sudokufile.txt"
```

Example 2:

```
argument=#	-> mandatory; user input; no default value provided for argument

$ git clone https://github.com/sandulachedumitru/JCmdArgs.git
- user must input value for the argument because is mandatory
```
