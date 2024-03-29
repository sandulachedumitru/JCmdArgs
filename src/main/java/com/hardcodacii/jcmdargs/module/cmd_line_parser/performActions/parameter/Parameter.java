package com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.parameter;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.CmdLinePerformAction;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class Parameter implements CmdLinePerformAction {
	private static final Parameter INSTANCE = new Parameter();

	private Parameter() {
	}

	public static Parameter getInstance() {
		return INSTANCE;
	}

	@Override
	public void performAction(String str) {
		System.out.println("THis is the usage for parameter=" + str);
	}
}
