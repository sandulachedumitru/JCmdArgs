package com.hardcodacii.jcmdargs.global;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class SystemEnvironmentVariable {
	public static final String CMD_OPTION_PREFIX = "--";

	public static final String REGEX_FOR_CMD_OPTIONS = "^(--([a-zA-Z]+[^0-9\\W]))(=((\\w+)(\\.([a-zA-Z0-9]+))?))?$";
	//public static final String REGEX_FOR_CMD_OPTIONS = "^(--([a-zA-Z]+[^0-9\\W]))(=(\\w+))?$";
	public static final String REGEX_FOR_CMD_FILE_PARAMETERS = "^(\\w+)(\\.([a-zA-Z0-9]+))?$"; // "^([a-zA-Z0-9]+)(\\.([a-zA-Z0-9]+))?$"
	public static final String REGEX_FOR_CMD_PARAMETERS = "^(\\w+)?$";

	public static String SUDOKU_INPUT_FILE = "SudokuResults.txt";
	public static boolean DUBUG_ENABLE = false;
}
