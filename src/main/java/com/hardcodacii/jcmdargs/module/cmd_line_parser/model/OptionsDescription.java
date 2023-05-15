package com.hardcodacii.jcmdargs.module.cmd_line_parser.model;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.performActions.CmdLinePerformAction;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Getter
@ToString
public class OptionsDescription {
	boolean acceptsUserInputValue; // used by options that expect user input value (ex: --print=MyFile.txt )
	boolean supportOtherOptions; // ex --help don't support other options
	String[] supportedValues; // ex --debug=enable
	String defaultSupportedValue; // ex: --debug=disable
	CmdLinePerformAction performer;
	private String optionName, usageHelp, messageSignalsTheUseOfDefaultValue;
}
