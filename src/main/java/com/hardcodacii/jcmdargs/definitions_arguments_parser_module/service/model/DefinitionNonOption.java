package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model;

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
