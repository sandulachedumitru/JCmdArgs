package com.hardcodacii.jcmdargs.service.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class DefinitionNonOption extends Definition {
	private DefinitionType type; // ex: ARGUMENT
	private List<String> possibleValues = new ArrayList<>(); // ex: ExampleFile.txt OR {ExampleFile.txt, #}
}
