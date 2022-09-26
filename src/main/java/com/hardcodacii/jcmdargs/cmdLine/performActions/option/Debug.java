package com.hardcodacii.jcmdargs.cmdLine.performActions.option;

import com.hardcodacii.utils.cmdLine.performActions.CmdOptionPerformAction;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class Debug implements CmdOptionPerformAction {
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
