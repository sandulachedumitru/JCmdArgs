package com.hardcodacii.jcmdargs.module.cmd_line_parser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Getter
@Setter
@ToString
public class CmdArgInfo {
	private String cmdArgument; // ex: cmdArgument=--printTo=DefaultSudokuFile.txt
	private boolean hasValidPattern = false;
	private boolean isSupported = false; // relevant only for cmd options
	private CmdLineOptionProperties properties;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CmdArgInfo that = (CmdArgInfo) o;
		return Objects.equals(properties, that.properties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(properties);
	}
}
