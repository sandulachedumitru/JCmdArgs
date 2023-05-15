package com.hardcodacii.jcmdargs.module.cmd_line_parser.service.impl;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.model.*;
import com.hardcodacii.jcmdargs.module.cmd_line_parser.service.CommandLineParserService;
import com.hardcodacii.jcmdargs.module.commons.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.Definition;
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

		cmdParameterProcessor(arguments);
		return Optional.empty();
	}

	private void cmdParameterProcessor(String[] args) {
		// parse the command line parameters
		displayService.infoLn("Get parameters info by parsing the command line");
		var infoList = parseCmdLineParameters(args);

		// Number of occurrences for parameters
		displayService.infoLn("Number of occurrences for parameter");
		var occurrences = getDuplicates(infoList);
		displayService.emptyLine();

		errorService.displayErrors();
	}

	public List<CmdLineParamInfo> parseCmdLineParameters(String... args) {
		List<CmdLineParamInfo> infoList = new ArrayList<>();

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

				infoList.add(info);
			} else {
				displayService.infoLn("\t\tPARAMETER: {}", arg);
				info.setExpression(arg);
				infoList.add(info);
			}

			displayService.emptyLine();
		}

		return infoList;
	}

	private Map<CmdLineDuplicates, Integer> getDuplicates(List<CmdLineParamInfo> infoList) {
		Map<CmdLineDuplicates, Integer> duplicates = new HashMap<>();

		for (var info : infoList) {
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
}
