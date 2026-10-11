package com.hardcodacii.jcmdargs.module.cmd_line_parser.service.impl;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.model.*;
import com.hardcodacii.jcmdargs.module.cmd_line_parser.service.CommandLineParserService;
import com.hardcodacii.jcmdargs.module.commons.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionNonOption;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionOption;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionType;
import com.hardcodacii.logsindentation.service.DisplayService;
import com.hardcodacii.logsindentation.service.ErrorService;
import com.hardcodacii.logsindentation.service.model.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class CommandLineParserServiceImpl implements CommandLineParserService {
	private final SystemEnvironmentVariable environment;
	private final DisplayService displayService;
	private final ErrorService errorService;

	@Override
	public Optional<CmdLineArgsActionPerformer> parseCmdLine(String pathToDefinitionsFile, Map<DefinitionType, List<Definition>> definitionsMap) {
		displayService.infoLn("PARSE THE COMMAND LINE PARAMETERS");

		String[] arguments = {
				"argument1",
				"argument2",
				"command",
				"--help",
				"--skipTest",
				"--skipTest=true",
				"--inputFile=MySudokuFile.txt"
		};

		cmdParameterProcessor(arguments, definitionsMap);
		return Optional.empty();
	}

	private void cmdParameterProcessor(String[] args, Map<DefinitionType, List<Definition>> definitionsMap) {
		// parse the command line parameters
		displayService.infoLn("Get parameters info by parsing the command line");
		var infoList = parseCmdLineParameters(args);

		// Number of occurrences for parameters
		displayService.infoLn("Number of occurrences for parameter");
		var occurrences = getDuplicates(infoList);
		displayService.emptyLine();

		// check supported parameter and value for options
		displayService.infoLn("Check supported parameters and values for options");
		var supported = getSupported(infoList, definitionsMap);
		displayService.emptyLine();

		errorService.displayErrors();
	}

	public List<CmdLineParamInfo> parseCmdLineParameters(String... args) {
		List<CmdLineParamInfo> paramInfoList = new ArrayList<>();

		var regexCmdOptions = environment.REGEX_OPTION_GENERAL;
		var patternCmdOption = Pattern.compile(regexCmdOptions);

		for (var arg : args) {
			var info = new CmdLineParamInfo();
			info.setExpression(arg);

			displayService.infoLn("parameter: {}\t", arg);
			var matcherCmdOption = patternCmdOption.matcher(arg);
			if (matcherCmdOption.find()) {
				/*
					^((--?)([a-zA-Z]+[^0-9\W]))(=((\w+)(\.([a-zA-Z0-9]+))?))?$



						arg: --skipTest		groupCount: 8
							OPTION: group()--> --skipTest
							OPTION: group[0]--> --skipTest

							OPTION: group[1]--> --skipTest
							OPTION: group[2]--> --
							OPTION: group[3]--> skipTest
							OPTION: group[4]-->
							OPTION: group[5]-->
							OPTION: group[6]-->
							OPTION: group[7]-->
							OPTION: group[8]-->



						arg: --skipTest=true		groupCount: 8
							OPTION: group()--> --skipTest=true
							OPTION: group[0]--> --skipTest=true

							OPTION: group[1]--> --skipTest
							OPTION: group[2]--> --
							OPTION: group[3]-->	skipTest
							OPTION: group[4]--> =true
							OPTION: group[5]--> true
							OPTION: group[6]--> true
							OPTION: group[7]-->
							OPTION: group[8]-->



						arg: --skipTest=true.txt		groupCount: 8
							OPTION: group()--> --skipTest=true.txt
							OPTION: group[0]--> --skipTest=true.txt

							OPTION: group[1]--> --skipTest
							OPTION: group[2]--> --
							OPTION: group[3]-->	skipTest
							OPTION: group[4]--> =true.txt
							OPTION: group[5]--> true.txt
							OPTION: group[6]--> true
							OPTION: group[7]--> .txt
							OPTION: group[8]--> txt
				*/
				displayService.infoLn("Start index: {}", matcherCmdOption.start());
				displayService.infoLn(" End index: {}\t", matcherCmdOption.end());
				displayService.infoLn("groupCount: {}", matcherCmdOption.groupCount());
				displayService.infoLn("\t\tOPTION: group()--> " + matcherCmdOption.group());
				for (var g = 0; g <= matcherCmdOption.groupCount(); g++) {
					displayService.infoLn("\t\tOPTION: group[" + g + "]--> " + matcherCmdOption.group(g));
				}

				var optName = matcherCmdOption.group(1);
				var optValue = matcherCmdOption.group(5);

				var properties = new CmdLineOptionProperties();
				properties.setName(optName);
				properties.setValue(optValue);

				info.setType(CmdLineParamType.OPTION);
				info.setOptsProperties(properties);

				paramInfoList.add(info);
			} else {
				displayService.infoLn("\t\tPARAMETER: {}", arg);
				info.setExpression(arg);
				paramInfoList.add(info);
			}

			displayService.emptyLine();
		}

		return paramInfoList;
	}

	private Map<CmdLineDuplicates, Integer> getDuplicates(List<CmdLineParamInfo> paramInfoList) {
		Map<CmdLineDuplicates, Integer> duplicates = new HashMap<>();

		for (var info : paramInfoList) {
			var dupl = new CmdLineDuplicates();

			// check duplicated options
			if (info.getType() == CmdLineParamType.OPTION) {
				var optionName = info.getOptsProperties().getName();
				dupl.setValue(optionName);
			}

			// check duplicated command/arguments
			else {
				var paramName = info.getExpression();
				dupl.setValue(paramName);
			}

			dupl.setType(info.getType());

			if (duplicates.containsKey(dupl)) {
				var occurrences = duplicates.get(dupl);
				duplicates.put(dupl, ++occurrences);
			} else {
				duplicates.put(dupl, 1);
			}

		}

		// logs
		for (var entry : duplicates.entrySet()) {
			var duplValue = entry.getKey().getValue();
			var duplType = entry.getKey().getType();
			var numberOfOccurrences = entry.getValue();
			displayService.infoLn("[value: {}, type: {}] --> [{}]", duplValue, duplType, numberOfOccurrences);

			if (numberOfOccurrences > 1) {
				var msg = "Command line parameter: [" + duplValue + ", " + duplType + "] has duplicates: [" + numberOfOccurrences + "]";
				errorService.addError(new Error(msg));
			}
		}

		return duplicates;
	}

	private List<CmdLineSupportedParam> getSupported(List<CmdLineParamInfo> paramInfoList, Map<DefinitionType, List<Definition>> definitionsMap) {
		List<CmdLineSupportedParam> supportedParamList = new ArrayList<>();

		for (var paramInfo : paramInfoList) {
			var supportedParam = new CmdLineSupportedOptionAndValues();

			// check parameters: OPTION
			if (paramInfo.getType() == CmdLineParamType.OPTION) {
				supportedParam = checkCmdLineOptionNameAndValue(paramInfo, definitionsMap);
			}

			// check parameters: ARGUMENT, COMMAND
			else {
				supportedParam = checkCmdLineNonOptionParameter(paramInfo, definitionsMap);
			}

			supportedParamList.add(supportedParam);
		}

		return supportedParamList;
	}

	private static CmdLineSupportedOptionAndValues checkCmdLineOptionNameAndValue(CmdLineParamInfo paramInfo, Map<DefinitionType, List<Definition>> definitionsMap) {
		var SUPPORTED = true;
		var NOT_SUPPORTED = false;

		var isOptNameSupported = NOT_SUPPORTED;
		DefinitionOption defIdentifiedOpt = null;

		var optDefList = definitionsMap.get(DefinitionType.OPTION);
		var suppParam = new CmdLineSupportedOptionAndValues();
		suppParam.setParamInfo(paramInfo);

		// check cmd line option name
		for (var definition : optDefList) {
			if (! (definition instanceof DefinitionOption)) break;
			var def = (DefinitionOption) definition;
			if (def.getOptsDefinitions().contains(paramInfo.getOptsProperties().getName())) {
				isOptNameSupported = SUPPORTED;
				defIdentifiedOpt = def;
				break;
			}
		}

		// check cmd line option value
		if (isOptNameSupported) {
			var isOptValueSupported = NOT_SUPPORTED;
			var defAllowedValues = defIdentifiedOpt.getAllowedValues();
			var paramInfoValue = paramInfo.getOptsProperties().getValue();

			if (defAllowedValues.contains(paramInfoValue)) {
				isOptValueSupported = SUPPORTED;
			} else {
				suppParam.setUnsupportedValue(paramInfoValue);
			}

			suppParam.setSupported(isOptValueSupported);
		} else {
			suppParam.setUnsupportedOptionsDef(paramInfo.getOptsProperties().getName());
		}

		return suppParam;
	}

	private static CmdLineSupportedOptionAndValues checkCmdLineNonOptionParameter(CmdLineParamInfo paramInfo, Map<DefinitionType, List<Definition>> definitionsMap) {
		var SUPPORTED = true;
		var NOT_SUPPORTED = false;

		var isOptNameSupported = NOT_SUPPORTED;
		DefinitionNonOption defIdentifiedOpt = null;

		var argDefList = definitionsMap.get(DefinitionType.ARGUMENT);
		var suppParam = new CmdLineSupportedOptionAndValues();
		suppParam.setParamInfo(paramInfo);

		// check parameters: ARGUMENT, COMMAND, UNKNOWN
		for (var definition : argDefList) {
			if (! (definition instanceof DefinitionNonOption)) break;
			var def = (DefinitionNonOption) definition;
			if (def.getPossibleValues().contains(paramInfo.getOptsProperties().getName())) {
				isOptNameSupported = SUPPORTED;
				defIdentifiedOpt = def;
				break;
			}
		}

		return suppParam;
	}
}
