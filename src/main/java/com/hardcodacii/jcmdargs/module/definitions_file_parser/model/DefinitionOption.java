package com.hardcodacii.jcmdargs.module.definitions_file_parser.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class DefinitionOption extends Definition {
	boolean singleOption = false;
	private List<String> allowedValues = new ArrayList<>(); // ex: {enable,disable} OR ExampleFile.txt
	private List<String> optsDefinitions = new ArrayList<>(); // ex: {--debug,-d}
}
