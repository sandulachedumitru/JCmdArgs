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
public class CmdLineParamInfo {
	private String expression; // ex: [--debug=enable] / [clone] / [12345/DefaultSudokuFile.txt/argument]
	private CmdLineParamType type = CmdLineParamType.UNSUPPORTED;
	private CmdLineOptionProperties optsProperties;
	CmdLinePerformAction performer;
}
