package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model;

import java.util.stream.Stream;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public enum DefinitionType {
	OPTION("option"),
	COMMAND("command"),
	ARGUMENT("argument"),
	ARGUMENTS_NUMBER("arguments_number"),
	ALLOWED_ARGUMENTS_ORDER("allowed_arguments_order");

	private final String argumentCode;

	DefinitionType(String argumentCode) {
		this.argumentCode = argumentCode;
	}

	public static DefinitionType getDefinitionTypeByCode(String argumentCode) {
		var argumentTypeOpt = Stream.of(DefinitionType.values())
				.filter(argumentType -> argumentType.getArgumentCode().equalsIgnoreCase(argumentCode))
				.findFirst();
		return argumentTypeOpt.orElse(null);
	}

	public String getArgumentCode() {
		return argumentCode;
	}
}
