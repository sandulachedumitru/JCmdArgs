package com.hardcodacii.jcmdargs.module.cmd_line_parser.model;

import java.util.stream.Stream;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public enum CmdLineParamType {
	OPTION("option"),
	COMMAND("command"),
	ARGUMENT("argument"),
	UNSUPPORTED("unsupported");

	private final String paramCode;

	CmdLineParamType(String argumentCode) {
		this.paramCode = argumentCode;
	}

	public static CmdLineParamType getTypeByCode(String argumentCode) {
		var argumentTypeOpt = Stream.of(CmdLineParamType.values())
				.filter(argumentType -> argumentType.getParamCode().equalsIgnoreCase(argumentCode))
				.findFirst();
		return argumentTypeOpt.orElse(null);
	}

	public String getParamCode() {
		return paramCode;
	}
}
