package com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.impl;

import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.ResourcesGeneratorService;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.DefinitionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class ResourcesGeneratorServiceImpl implements ResourcesGeneratorService {

	@Override
	public Optional<Boolean> generateResources(Map<DefinitionType, List<Definition>> definitionsMap) {
		return Optional.empty();
	}
}
