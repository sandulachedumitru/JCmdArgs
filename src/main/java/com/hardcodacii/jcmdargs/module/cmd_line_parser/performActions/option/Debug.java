package com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.option;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.CmdLinePerformAction;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class Debug implements CmdLinePerformAction {
	private static final Debug INSTANCE = new Debug();

	private Debug() {
	}

	public static Debug getInstance() {
		return INSTANCE;
	}

	@Override
	public void performAction(String str) {
		System.out.println("THis is the usage for --debug=" + str);
	}
}
