package com.hardcodacii.jcmdargs.service;

import com.hardcodacii.jcmdargs.service.model.Argument;

import java.util.Map;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface CmdLineDefinitionParserService {
	Map<String, Argument> parseArguments();
}
