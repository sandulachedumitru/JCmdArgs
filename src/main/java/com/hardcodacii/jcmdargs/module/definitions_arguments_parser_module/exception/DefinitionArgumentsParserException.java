package com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.exception;

import com.hardcodacii.jcmdargs.module.commons_module.exception.CmdArgsLineException;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class DefinitionArgumentsParserException extends CmdArgsLineException {
	public DefinitionArgumentsParserException(String errorMessage) {
		super(errorMessage);
	}
}
