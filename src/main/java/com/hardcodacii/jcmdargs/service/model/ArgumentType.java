package com.hardcodacii.jcmdargs.service.model;

import java.util.stream.Stream;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public enum ArgumentType {
	OPTION("option"),
	COMMAND("command"),
	ARGUMENT("argument"),
	ARGUMENTS_NUMBER("arguments_number"),
	ALLOWED_ARGUMENTS_ORDER("allowed_arguments_order");

	private String argumentCode;
	ArgumentType(String argumentCode) {
		this.argumentCode = argumentCode;
	}

	public String getArgumentCode() {
		return argumentCode;
	}

	public static ArgumentType getArgumentTypeByCode(String argumentCode) {
		var argumentTypeOpt = Stream.of(ArgumentType.values())
				.filter(argumentType -> argumentType.getArgumentCode().equalsIgnoreCase(argumentCode))
				.findFirst();
		return argumentTypeOpt.orElse(null);
	}
}
