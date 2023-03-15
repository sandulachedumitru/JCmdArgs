package com.hardcodacii.jcmdargs.program_arguments_parser_module.cmdLine.performActions.parameter;

import com.hardcodacii.jcmdargs.program_arguments_parser_module.cmdLine.performActions.CmdOptionPerformAction;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class Parameter implements CmdOptionPerformAction {
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
