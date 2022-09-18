# JCmdArgs

## Java Command Line Arguments Processor

### cmd general rule:

### `[[!]--<option>[={<value1>,<value2>, ...}]] [<command>] [<argument>] [<arguments_number>] {option, command, arguments_number, argument}`

### `option=[[!]--<option>[={<value1>,<value2>, ...}]] command=[<command>] argument=[<argument>] arguments_number=[<number>] precedences={option, command, arguments_number, argument}`

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
