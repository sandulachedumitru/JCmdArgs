package com.hardcodacii.jcmdargs.service;

import com.hardcodacii.jcmdargs.service.model.Argument;
import com.hardcodacii.jcmdargs.service.model.ArgumentType;

import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface CmdLineDefinitionParserService {
	Optional<Map<ArgumentType, Argument>> parseArguments();
}
