package com.hardcodacii.jcmdargs.module.cmd_line_parser.model;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.CmdLinePerformAction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Getter
@Setter
@ToString
public class CmdLineDuplicates {
	private String value; // ex: [--debug] / [clone] / [12345/DefaultSudokuFile.txt/argument]
	private CmdLineParamType type = CmdLineParamType.UNSUPPORTED;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CmdLineDuplicates that = (CmdLineDuplicates) o;

		if (!value.equals(that.value)) return false;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		int result = value.hashCode();
		result = 31 * result + type.hashCode();
		return result;
	}
}
