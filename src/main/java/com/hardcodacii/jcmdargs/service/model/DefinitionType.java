package com.hardcodacii.jcmdargs.service.model;

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

	private String argumentCode;
	DefinitionType(String argumentCode) {
		this.argumentCode = argumentCode;
	}

	public String getArgumentCode() {
		return argumentCode;
	}

	public static DefinitionType getDefinitionTypeByCode(String argumentCode) {
		var argumentTypeOpt = Stream.of(DefinitionType.values())
				.filter(argumentType -> argumentType.getArgumentCode().equalsIgnoreCase(argumentCode))
				.findFirst();
		return argumentTypeOpt.orElse(null);
	}
}
