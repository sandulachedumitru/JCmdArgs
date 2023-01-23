package com.hardcodacii.jcmdargs.service;

import com.hardcodacii.jcmdargs.service.model.DefinitionOption;
import com.hardcodacii.jcmdargs.service.model.DefinitionType;

import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface CmdLineDefinitionParserService {
	Optional<Map<DefinitionType, DefinitionOption>> parseDefinitionFile();
}
