package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model;

import lombok.Data;

/**
 * Marker interface
 *
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class DefinitionPropertiesParser {
	String definition; // ex: option={--debug,-d}={enable,disable} OR argument=ExampleFile.txt
	String definitionType; // ex: option OR argument
	String allowedValues; // ex: {enable,disable} OR ExampleFile.txt
}
