package com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service;

import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.DefinitionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface ResourcesGeneratorService {
	Optional<Boolean> generateResources(Map<DefinitionType, List<Definition>> definitionsMap);
}
