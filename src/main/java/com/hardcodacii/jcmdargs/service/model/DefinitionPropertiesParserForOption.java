package com.hardcodacii.jcmdargs.service.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
@ToString(callSuper = true)
public class DefinitionPropertiesParserForOption extends DefinitionPropertiesParser {
	String option; // ex: {--help,-h}
	boolean singleOption = false;
}
