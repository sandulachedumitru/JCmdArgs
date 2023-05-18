package com.hardcodacii.jcmdargs.module.definitions_file_parser.service;

import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface ResourcesGeneratorService {
	Optional<Set<String>> generateResources(String pathToDefinitionFile, Map<DefinitionType, List<Definition>> definitionsMap);
}
