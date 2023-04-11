package com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service;

import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.DefinitionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@FunctionalInterface
public interface CmdLineDefinitionParserService {
	Optional<Map<DefinitionType, List<Definition>>> parseDefinitionFile(String definitionsFile);
}
