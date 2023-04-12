package com.hardcodacii.jcmdargs.module.definitions_file_parser.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
@ToString(callSuper = true)
public class DefinitionPropertiesParserForOption extends DefinitionPropertiesParser {
	String option; // ex: {--help,-h}
}
