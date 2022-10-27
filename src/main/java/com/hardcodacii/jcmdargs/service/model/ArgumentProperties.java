package com.hardcodacii.jcmdargs.service.model;

import lombok.Data;

/**
 * Marker interface
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class ArgumentProperties {
	String definition; // ex: option={--debug,-d}={enable,disable} OR argument=ExampleFile.txt
	String argumentType; // ex: option OR argument
	String optionAllowedValues; // ex: {enable,disable} OR ExampleFile.txt
}
