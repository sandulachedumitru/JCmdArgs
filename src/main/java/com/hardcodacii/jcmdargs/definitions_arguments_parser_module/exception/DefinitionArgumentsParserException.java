package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.exception;

import com.hardcodacii.jcmdargs.commons_module.exception.CmdArgsLineException;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class DefinitionArgumentsParserException extends CmdArgsLineException {
	public DefinitionArgumentsParserException(String errorMessage) {
		super(errorMessage);
	}
}
