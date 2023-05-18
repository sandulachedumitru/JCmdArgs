package com.hardcodacii.jcmdargs.module.cmd_line_parser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Getter
@Setter
@ToString
public class CmdLineSupportedParam {
	private CmdLineParamInfo infoList;
	private boolean isSupported = false; // relevant only for cmd options
}
