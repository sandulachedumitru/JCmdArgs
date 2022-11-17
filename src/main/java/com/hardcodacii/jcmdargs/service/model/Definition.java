package com.hardcodacii.jcmdargs.service.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class Definition {
	private DefinitionType type; // ex: OPTION
	private List<String> allowedValues = new ArrayList<>(); // ex: {enable,disable} OR ExampleFile.txt
	private List<String> optsDefinitions = new ArrayList<>(); // ex: {--debug,-d}
}
