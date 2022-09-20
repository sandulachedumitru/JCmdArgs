> # JCmdArgs

## Java Command Line Arguments Processor

### General form:

`{option, command, arguments_number, argument}`
`option=[[!]{--<optionLong>,-<optionShort>}[={}]]`
`command=[<command>]`
`argument=[<argument>]`
`arguments_number=[<number>]`
### `OR`
### `{option, command, arguments_number, argument}`
### `option=[[!]{--<optionLong>,-<optionShort>}[={}]]`
### `command=[<command>]`
### `argument=[<argument>]`
### `arguments_number=[<number>]`

> **NOTE**: No space character will be allowed inside element

## Special characters
| Symbol                  | Description                                         |
|-------------------------|-----------------------------------------------------|
| `[]`                    | `is optional`                                       |
| `={value1,value2, ...}` | `he list of supported values of the option`         |
| `<something>`           | `replace with real word`                            |
| `!`                     | `cannot support other option; is the only argument` |
| `option=`               | `defines an option`                                 |
| `command=`              | `defines a command`                                 |
| `argument=`             | `defines an argument`                               |
| `arguments_number=`     | `the number of arguments allowed`                   |
