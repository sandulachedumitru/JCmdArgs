package com.hardcodacii.jcmdargs.module.definitions_file_parser.model;

import lombok.Data;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class DefinitionParser {
	private DefinitionType type;
	private DefinitionPropertiesParser properties;
}
