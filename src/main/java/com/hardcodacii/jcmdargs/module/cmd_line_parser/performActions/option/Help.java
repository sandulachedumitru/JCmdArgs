package com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.option;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.CmdOptionPerformAction;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class Help implements CmdOptionPerformAction {
	private static final Help INSTANCE = new Help();

	private Help() {
	}

	public static Help getInstance() {
		return INSTANCE;
	}

	@Override
	public void performAction(String str) {
		System.out.println("THis is the usage for --help=" + str);
	}
}
