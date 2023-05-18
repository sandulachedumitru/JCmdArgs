package com.hardcodacii.jcmdargs.API_connector.service;

import com.hardcodacii.jcmdargs.module.definitions_file_parser.exception.DefinitionArgumentsParserException;

import java.io.FileNotFoundException;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface CommandLineService {
	void getDefinitionsFromFile(String definitionsFile) throws DefinitionArgumentsParserException, FileNotFoundException;
	void processParametersFromCmdLine(String... args);
}
