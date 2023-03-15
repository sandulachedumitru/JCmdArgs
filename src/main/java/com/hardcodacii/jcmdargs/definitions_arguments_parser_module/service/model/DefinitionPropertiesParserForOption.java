package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model;

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
