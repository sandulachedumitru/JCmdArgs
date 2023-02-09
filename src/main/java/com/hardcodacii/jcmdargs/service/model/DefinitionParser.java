package com.hardcodacii.jcmdargs.service.model;

import lombok.Data;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class DefinitionParser {
	private DefinitionType type;
	private DefinitionPropertiesParser properties;
}
