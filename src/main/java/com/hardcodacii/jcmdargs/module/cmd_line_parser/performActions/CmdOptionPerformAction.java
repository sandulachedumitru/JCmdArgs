package com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@FunctionalInterface
public interface CmdOptionPerformAction {
	void performAction(String str);
}
