package com.hardcodacii.jcmdargs.module.cmd_line_parser.service;

import com.hardcodacii.jcmdargs.module.commons.global.SystemEnvironmentVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class CommandLineParametersServiceImpl2 {
	private final SystemEnvironmentVariable environment;

//	private String APP_NAME = environment.APP_NAME;
//	private String CMD_OPTION_PREFIX = environment.REGEX_OPTION_LONG;

//	public static void main(String... args) {
//		System.out.println(CmdOptionsDefinition.sanitizeSingleSupportedOptionValue("-=ena bl -e+"));
//		System.out.println(CmdOptionsDefinition.sanitizeSingleSupportedOptionValue("disable"));
//
//		String[] arguments = {
//				//"-abc",
//				//"--skipTest=true",
//				"--printTo",
//				"--debug",
//				//"--debug",
//				//"--debug=enable",
//				"MySudokuFile.txt",
//				//"--help=true",
//				"--help"
//		};
//
//		cmdParameterProcessor(arguments);
//	}
//
//	public void cmdParameterProcessor(String... args) {
//		// definitions and initialization of the supported cmd options
//		CmdOptionsDefinition.defineCmdOptions();
//		System.out.println("cmd options [size: " + CmdOptionsDefinition.optDesc.size() + "]");
//		for (var od : CmdOptionsDefinition.optDesc)
//			System.out.println("\t" + od);
//
//		// analyzing the app arguments
//		var areFormatted = CmdArgsParser.areAllParametersFormatted(args);
//		System.out.println("areAllParametersFormatted(args): " + areFormatted);
//		System.out.println("parsedArgumentsInfo: ");
//		for (var argsInfo : CmdArgsParser.parsedArgumentsInfo)
//			System.out.println("\t" + argsInfo);
//
//		// perform action
//		boolean areManyOccurrences = false;
//		System.out.println("Number of occurrences for:");
//		for (var entry : CmdArgsParser.numberOfOccurrences.entrySet()) {
//			System.out.println("\t[arg:" + entry.getKey() + "]\t[occurrences:" + entry.getValue() + "]");
//			if (entry.getValue() > 1) areManyOccurrences = true;
//		}
//		boolean isUnsupportedArg = false;
//		System.out.println("Arguments support:");
//		for (var info : CmdArgsParser.parsedOptionsInfo) {
//			if (info.isSupported)
//				System.out.println("\targ: [" + info.properties.name + "] SUPPORTED");
//			else {
//				System.out.println("\targ: [" + info.properties.name + "] unsupported");
//				isUnsupportedArg = true;
//			}
//		}
//
//		if (areFormatted) {
//			System.out.println("The cmd arguments are formatted");
//			if (!areManyOccurrences) {
//				// check if every option are supported
//				System.out.println("Unsupported arguments:");
//				if (isUnsupportedArg) {
//					for (var info : CmdArgsParser.parsedOptionsInfo) {
//						if (!info.isSupported)
//							System.out.println("\targ: [" + info.cmdArgument + "]");
//					}
//				} else System.out.println("\tAll arguments are supported");
//
//				System.out.println("Check if the values of options are valid");
//				boolean isValidValue = true;
//				for (var info : CmdArgsParser.parsedOptionsInfo) {
//					System.out.print("\toption:[" + info.properties.name + "] -> value:[" + info.properties.value + "] -> ");
//					if (info.properties.isOption)
//						if (isOptionValueDefined(info.properties)) {
//							System.out.println("VALID VALUE");
//						} else {
//							System.out.println("INVALID VALUE");
//							isValidValue = false;
//						}
//					else System.out.println("NOT AN OPTION !!!");
//				}
//
//				if (! isValidValue) return;;
//
//				System.out.println("The command - option - parameter:");
//				System.out.print("\tsudoku ");
//				for (var info : CmdArgsParser.parsedOptionsInfo) {
//					System.out.print(CMD_OPTION_PREFIX() + info.properties.name);
//					if (info.properties.value != null)
//						System.out.print("=" + info.properties.value);
//					System.out.print(" ");
//				}
//				for (var info : CmdArgsParser.parsedParametersInfo)
//					System.out.print(info.properties.name);
//				System.out.println();
//
//				System.out.println("PERFORM ACTIONS FOR OPTIONS AND PARAMETER");
//				for (var info : CmdArgsParser.parsedOptionsInfo) {
//					for (var optDesc : CmdOptionsDefinition.optDesc) {
//						if (info.properties.name.equalsIgnoreCase(optDesc.optionName)) {
//							optDesc.getPerformer().performAction(info.properties.value);
//							break;
//						}
//					}
//				}
//				for (var info : CmdArgsParser.parsedParametersInfo) {
//					CmdOptionPerformAction parameter = Parameter.getInstance();
//					parameter.performAction(info.properties.name);
//				}
//
//			} else {
//				System.out.println("Many occurrences for same arguments. Display --help usage. Exiting app ...");
//				for (var entry : CmdArgsParser.numberOfOccurrences.entrySet()) {
//					if (entry.getValue() > 1)
//						System.out.println("\targ: [" + entry.getKey() + "]\toccurrences: [" + entry.getValue() + "]");
//				}
//				// TODO display --help usage
//			}
//		} else {
//			System.out.println("The cmd arguments are not formatted. Display --help usage. Exiting app ...");
//			for (var puai : CmdArgsParser.parsedUnformattedArgumentsInfo)
//				if (!puai.hasValidPattern)
//					System.out.println("\tInvalid argument: [" + puai.cmdArgument + "]");
//			// TODO display --help usage
//		}
//	}
//
//	private static boolean isOptionValueDefined(CmdArgsParser.CmdArgumentProperties properties) {
//		for (var description : CmdOptionsDefinition.optDesc) {
//			if (description.optionName.equalsIgnoreCase(properties.name)) {
//				if (description.supportedValues == null)
//					if (description.acceptsUserInputValue) return true;
//					else if (properties.value != null) return false;
//					else return true;
//				else {
//					assert description.supportedValues != null;
//					for (var value : description.supportedValues)
//						if (value.equalsIgnoreCase(properties.value)) return true;
//					return false;
//				}
//			}
//		}
//		return false;
//	}
//
//	private class CmdArgsParser {
//		private final ArrayList<CmdArgsInfo> parsedOptionsInfo = new ArrayList<>();
//		private final ArrayList<CmdArgsInfo> parsedParametersInfo = new ArrayList<>();
//		private final ArrayList<CmdArgsInfo> parsedUnformattedArgumentsInfo = new ArrayList<>();
//		private final ArrayList<CmdArgsInfo> parsedArgumentsInfo = new ArrayList<>();
//		private final Map<String, Integer> numberOfOccurrences = new HashMap<>();
//
//		public boolean areAllParametersFormatted(String... args) {
//			var regexForCmdOptions = environment.REGEX_OPTION_LONG;
//			var regexForCmdParameters = environment.REGEX_PARAMETER_FILE;
//
//			var patternCmdOption = Pattern.compile(regexForCmdOptions);
//			var patternCmdParam = Pattern.compile(regexForCmdParameters);
//
//			boolean result = true;
//			for (var arg : args) {
//				var info = new CmdArgsInfo();
//
//				info.cmdArgument = arg;
//
//				System.out.print("arg: " + arg + "\t");
//				var matcherCmdOption = patternCmdOption.matcher(arg);
//				var matcherCmdParam = patternCmdParam.matcher(arg);
//
//				if (matcherCmdOption.find()) {
//                    /*
//						arg: --printTo=DefaultSudokuFile.txt	Start index: 0 End index: 31	groupCount: 7				arg: --debug=enable	Start index: 0 End index: 12	groupCount: 7
//								OPTION: group()--> --printTo=DefaultSudokuFile.txt													OPTION: group()--> --debug=enable
//								OPTION: group[0]--> --printTo=DefaultSudokuFile.txt													OPTION: group[0]--> --debug=enable
//								OPTION: group[1]--> --printTo																		OPTION: group[1]--> --debug
//								OPTION: group[2]--> printTo																			OPTION: group[2]--> debug
//								OPTION: group[3]--> =DefaultSudokuFile.txt															OPTION: group[3]--> =enable
//								OPTION: group[4]--> DefaultSudokuFile.txt															OPTION: group[4]--> enable
//								OPTION: group[5]--> DefaultSudokuFile																OPTION: group[5]--> enable
//								OPTION: group[6]--> .txt																			OPTION: group[6]--> null
//								OPTION: group[7]--> txt																				OPTION: group[7]--> null
//                    */
//					System.out.print("Start index: " + matcherCmdOption.start());
//					System.out.print(" End index: " + matcherCmdOption.end() + "\t");
//					System.out.println("groupCount: " + matcherCmdOption.groupCount());
//					System.out.println("\t\tOPTION: group()--> " + matcherCmdOption.group());
//					for (int g = 0; g <= matcherCmdOption.groupCount(); g++) {
//						System.out.println("\t\tOPTION: group[" + g + "]--> " + matcherCmdOption.group(g));
//					}
//
//					String optName = matcherCmdOption.group(2);
//					String optValue = matcherCmdOption.group(4);
//
//					// if no value for option then default value will be used
//					if (optValue == null) {
//						for (var optDesc : CmdOptionsDefinition.optDesc)
//							if (optDesc.optionName.equalsIgnoreCase(optName)) {
//								optValue = optDesc.defaultSupportedValue;
//								break;
//							}
//					}
//
//					CmdArgumentProperties properties = new CmdArgumentProperties();
//					properties.isOption = true;
//					properties.name = optName;
//					properties.value = optValue;
//
//					info.hasValidPattern = true;
//					info.isSupported = CmdOptionsDefinition.optDesc.stream().anyMatch(od -> od.optionName.equals(properties.name));
//					info.properties = properties;
//
//					parsedOptionsInfo.add(info);
//				} else if (matcherCmdParam.find()) {
//                    /*
//                        arg: MySudokyFile.txt	Start index: 0 End index: 16	groupCount: 3
//                            PARAMETER: group()--> MySudokyFile.txt
//                            PARAMETER: group[0]--> MySudokyFile.txt
//                            PARAMETER: group[1]--> MySudokyFile
//                            PARAMETER: group[2]--> .txt
//                            PARAMETER: group[3]--> txt
//                    */
//					System.out.print("Start index: " + matcherCmdParam.start());
//					System.out.print(" End index: " + matcherCmdParam.end() + "\t");
//					System.out.println("groupCount: " + matcherCmdParam.groupCount());
//					System.out.println("\t\tPARAMETER: group()--> " + matcherCmdParam.group());
//					for (int g = 0; g <= matcherCmdParam.groupCount(); g++) {
//						System.out.println("\t\tPARAMETER: group[" + g + "]--> " + matcherCmdParam.group(g));
//					}
//
//					CmdArgumentProperties properties = new CmdArgumentProperties();
//					properties.isOption = false;
//					properties.name = matcherCmdParam.group();
//					properties.value = null;
//
//					info.hasValidPattern = true;
//					info.isSupported = true;
//					info.properties = properties;
//
//					parsedParametersInfo.add(info);
//				} else {
//					System.out.println("NO MATCH !");
//
//					info.hasValidPattern = false;
//					info.isSupported = false;
//					info.properties = null;
//
//					parsedUnformattedArgumentsInfo.add(info);
//
//					result = false;
//				}
//
//				parsedArgumentsInfo.add(info);
//			}
//
//			// number of occurrences
//			// options
//			for (var info : parsedOptionsInfo) {
//				if (numberOfOccurrences.containsKey(info)) {
//					System.out.println("already exists: " + info.properties.name + "\n");
//					int numOfOcc = numberOfOccurrences.get(info.properties.name);
//					numberOfOccurrences.put(
//							info.properties.name,
//							++numOfOcc
//					);
//				} else numberOfOccurrences.put(info.properties.name, 1);
//			}
//			// parameters
//			if (parsedParametersInfo.size() > 0)
//				//numberOfOccurrences.put(parsedParametersInfo.get(0).properties.name, parsedParametersInfo.size());
//				numberOfOccurrences.put("<parameter>", parsedParametersInfo.size());
//
//			return result;
//		}
////
////		@Getter
////		@ToString
////		private class CmdArgsInfo {
////			private String cmdArgument; // ex: cmdArgument=--printTo=DefaultSudokuFile.txt
////			private boolean hasValidPattern = false;
////			private boolean isSupported = false; // relevant only for cmd options
////			private CmdArgumentProperties properties;
////
////			@Override
////			public boolean equals(Object o) {
////				if (this == o) return true;
////				if (o == null || getClass() != o.getClass()) return false;
////				CmdArgsInfo that = (CmdArgsInfo) o;
////				return Objects.equals(properties, that.properties);
////			}
////
////			@Override
////			public int hashCode() {
////				return Objects.hash(properties);
////			}
////		}
////
////		@Getter
////		@ToString
////		private class CmdArgumentProperties {
////			private boolean isOption = false; // true if cmd option (ex: --printTo); false if parameter (ex; SudokuInput.txt)
////			private String name; // ex: name=printTo
////			private String value; // ex: value=DefaultSudokuFile.txt
////
////			@Override
////			public boolean equals(Object o) {
////				if (this == o) return true;
////				if (o == null || getClass() != o.getClass()) return false;
////				CmdArgumentProperties that = (CmdArgumentProperties) o;
////				return isOption == that.isOption && Objects.equals(name, that.name);
////			}
////
////			@Override
////			public int hashCode() {
////				return Objects.hash(isOption, name);
////			}
////		}
//	}

//	private static class CmdOptionsDefinition {
//
//		private static final HashSet<OptionsDescription> optDesc = new HashSet<>();
//
//		public static void defineCmdOptions() {
//			// --help
//			storeOptionsProperties("help", null, false, false, null, Help.getInstance());
//
//			// --debug
//			String[] supportedDebugValues = new String[]{"disable", "enable"};
//			storeOptionsProperties("debug", supportedDebugValues, true, false, null, Debug.getInstance());
//
//			// --printTo
//			String userInputValue = "DefaultSudokuFile.txt";
//			storeOptionsProperties("printTo", null, true, true, userInputValue, PrintTo.getInstance());
//		}
//
//		private void storeOptionsProperties(
//				String optionName,
//				String[] supportedValues,
//				boolean supportOtherOptions,
//				boolean acceptsUserInputValue,
//				String userInputValue,
//				CmdOptionPerformAction performer
//		) {
//			String _optionName, _usageHelp = APP_NAME + " ", _messageSignalsTheUseOfDefaultValue;
//			String _defaultSupportedValue = userInputValue;
//			String[] _supportedValues;
//
//			if (optionName == null) {
//				System.out.println("Option name is null. Options will not be supported by CLI");
//				return;
//			}
//			_optionName = sanitizeSingleSupportedOptionValue(optionName);
//
//			_supportedValues = sanitizeAllSupportedOptionValues(supportedValues);
//			if (_supportedValues != null) {
//				_defaultSupportedValue = _supportedValues[0];
//				_messageSignalsTheUseOfDefaultValue = "The default value for option [" + _optionName + "] was set to [" + _defaultSupportedValue + "]";
//				for (var sv : _supportedValues) {
//					_usageHelp += CMD_OPTION_PREFIX + _optionName + "=" + sv + " OR ";
//				}
//				_usageHelp += CMD_OPTION_PREFIX + _optionName; // sudoku --debug
//			} else if (acceptsUserInputValue) {
//				_messageSignalsTheUseOfDefaultValue = "The parameter [" + _optionName + "] default value will be set by app properties";
//				_usageHelp += CMD_OPTION_PREFIX + _optionName + "=<userInputValue>" + " OR "; // sudoku --printTo=<userInputValue>
//				_usageHelp += CMD_OPTION_PREFIX + _optionName; // sudoku --debug
//			} else {
//				_messageSignalsTheUseOfDefaultValue = "The option [" + _optionName + "] do not support value";
//				_usageHelp += CMD_OPTION_PREFIX + _optionName; // sudoku --help
//			}
//
//			OptionsDescription optionDescription = new OptionsDescription();
//			optionDescription.optionName = _optionName;
//			optionDescription.usageHelp = _usageHelp;
//			optionDescription.messageSignalsTheUseOfDefaultValue = _messageSignalsTheUseOfDefaultValue;
//			optionDescription.acceptsUserInputValue = acceptsUserInputValue;
//			optionDescription.supportedValues = _supportedValues;
//			optionDescription.defaultSupportedValue = _defaultSupportedValue;
//			optionDescription.supportOtherOptions = supportOtherOptions;
//			optionDescription.performer = performer;
//			optDesc.add(optionDescription);
//		}
//
//		private static boolean isStringNullOrEmpty(String str) {
//			return str == null || str.equals("");
//		}
//
//		private static boolean isArrayNullOrEmpty(String[] strArray) {
//			return strArray == null || strArray.length == 0;
//		}
//
//		private static String[] sanitizeAllSupportedOptionValues(String[] supportedOptions) {
//			if (isArrayNullOrEmpty(supportedOptions)) return null;
//
//			String[] sanitizedSupportedOptions = new String[supportedOptions.length];
//			int paramCount = 1;
//			for (var s = 0; s < supportedOptions.length; s++) {
//				if (supportedOptions[s] == null || supportedOptions[s].equals(""))
//					sanitizedSupportedOptions[s] = "value" + paramCount++;
//				else
//					sanitizedSupportedOptions[s] = sanitizeSingleSupportedOptionValue(supportedOptions[s]);
//			}
//
//			return sanitizedSupportedOptions;
//		}
//
//		private static String sanitizeSingleSupportedOptionValue(String value) {
//			String regex = "[a-zA-Z0-9]+";
//
//			Pattern pattern = Pattern.compile(regex);
//			Matcher matcher = pattern.matcher(value);
//
//			StringBuilder sb = new StringBuilder();
//			while (matcher.find())
//				sb.append(matcher.group());
//
//			return sb.length() == 0 ? value : sb.toString();
//		}
//
//		@Getter
//		@ToString
//		private static class OptionsDescription {
//			boolean acceptsUserInputValue; // used by options that expect user input value (ex: --print=MyFile.txt )
//			boolean supportOtherOptions; // ex --help don't support other options
//			String[] supportedValues; // ex --debug=enable
//			String defaultSupportedValue; // ex: --debug=disable
//			CmdOptionPerformAction performer;
//			private String optionName, usageHelp, messageSignalsTheUseOfDefaultValue;
//		}
//	}
}
