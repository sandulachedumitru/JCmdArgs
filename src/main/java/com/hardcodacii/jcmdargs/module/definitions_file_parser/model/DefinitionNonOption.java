package com.hardcodacii.jcmdargs.module.definitions_file_parser.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class DefinitionNonOption extends Definition {
	private List<String> possibleValues = new ArrayList<>(); // ex: ExampleFile.txt OR {ExampleFile.txt, #}
}
