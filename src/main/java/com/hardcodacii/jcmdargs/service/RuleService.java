package com.hardcodacii.jcmdargs.service;

import com.hardcodacii.jcmdargs.service.model.Definition;
import com.hardcodacii.jcmdargs.service.model.DefinitionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@FunctionalInterface
public interface RuleService {
	Optional<List<Error>> applyRules(Map<DefinitionType, List<Definition>> definitionsMap);
}
