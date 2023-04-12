package com.hardcodacii.jcmdargs.module.definitions_file_parser.exception;

import com.hardcodacii.jcmdargs.module.commons.exception.CmdArgsLineException;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class DefinitionArgumentsParserException extends CmdArgsLineException {
	public DefinitionArgumentsParserException(String errorMessage) {
		super(errorMessage);
	}
}
