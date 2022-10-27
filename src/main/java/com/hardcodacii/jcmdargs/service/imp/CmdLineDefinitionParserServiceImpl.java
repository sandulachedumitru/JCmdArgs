package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.FileIOService;
import com.hardcodacii.jcmdargs.service.LogProcessorService;
import com.hardcodacii.jcmdargs.service.model.Argument;
import com.hardcodacii.jcmdargs.service.model.ArgumentProperties;
import com.hardcodacii.jcmdargs.service.model.ArgumentPropertiesForOption;
import com.hardcodacii.jcmdargs.service.model.ArgumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class CmdLineDefinitionParserServiceImpl implements CmdLineDefinitionParserService {
	private final FileIOService fileIOService;
	private final SystemEnvironmentVariable environmentService;
	private final DisplayService displayService;
	private final LogProcessorService logProcessorService;

	private final Map<ArgumentType, List<Argument>> parsedArgumentsMap = new HashMap<>();

	@Override
	public Optional<Map<ArgumentType, Argument>> parseArguments() {
		var file = environmentService.PATH_TO_RESOURCES + environmentService.FILE_DEFINITION;
		var fileExists = fileIOService.fileExistsInResources(file);

		if (fileExists) {
			String fileContent = fileIOService.readStringFromFileInResources(file);
			System.out.println("fileContent: " + fileContent);

			Scanner scanner = new Scanner(fileContent);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String lineTrim = line.trim();
				System.out.println("line: " + lineTrim);
				lineProcessor(lineTrim);
			}
			scanner.close();
		}

		for (var entry : parsedArgumentsMap.entrySet()) {
			System.out.println("\tkey: " + entry.getKey() + "\tvalues: " + entry.getValue());
			for (var arg : entry.getValue()) {
				System.out.println("\t\tvalue: " + arg);

			}
		}

		return Optional.empty();
	}

	private Optional<Map<ArgumentType, List<Argument>>> lineProcessor(String line) {
		var EMPTY_LINE = environmentService.REGEX_GLOBAL_EMPTY_TEXT;

		var comment = environmentService.REGEX_SPECIAL_CHAR_SHARP;
		if (line == null || line.equals("") || line.startsWith(comment)) return Optional.empty();

		var regex = environmentService.REGEX_DEFINITION_LINE_ARGUMENT_MIX;
		var pattern = Pattern.compile(regex);
		var matcher = pattern.matcher(line);
		int NUMBER_OF_GROUPS = 5 + 1;

		List<Argument> argumentList = new ArrayList<>();
		while (matcher.find()) {
			for (var i = 0; i <= matcher.groupCount(); i++) {
				System.out.println("\t\tARGUMENT DEFINITION: group[" + i + "]--> " + matcher.group(i));
			}
			if (matcher.groupCount() != NUMBER_OF_GROUPS -1) {
				displayService.showlnErr(logProcessorService.processLogs("The number of matching groups is not good. Expected {} but is actually {}", String.valueOf(NUMBER_OF_GROUPS), String.valueOf(matcher.groupCount())));
				return Optional.empty();
			}

			Argument argument = new Argument();
			if (matcher.group(NUMBER_OF_GROUPS -2) == null && matcher.group(NUMBER_OF_GROUPS -1) == null) {
				int groupIndex = 0;
				ArgumentPropertiesForOption properties = new ArgumentPropertiesForOption();
				properties.setDefinition(matcher.group(groupIndex++));
				properties.setArgumentType(matcher.group(groupIndex++));
				properties.setOptionDefinition(matcher.group(groupIndex++));
				properties.setOptionAllowedValue(matcher.group(groupIndex++));

				var  type = ArgumentType.getArgumentTypeByCode(properties.getArgumentType());
				if (type != null) {
					argument.setType(type);
					argument.setProperties(properties);
				} else {
					displayService.showlnErr(logProcessorService.processLogs("The argument type '{}' is unknown and will be ignored.", properties.getArgumentType()));
				}

			} else {
				int groupIndex = NUMBER_OF_GROUPS;
				ArgumentProperties properties = new ArgumentProperties();
				properties.setDefinition(matcher.group(0));
				properties.setOptionAllowedValue(matcher.group(--groupIndex));
				properties.setArgumentType(matcher.group(--groupIndex));

				var  type = ArgumentType.getArgumentTypeByCode(properties.getArgumentType());
				if (type != null) {
					argument.setType(type);
					argument.setProperties(properties);
				} else {
					displayService.showlnErr(logProcessorService.processLogs("The argument type '{}' is unknown and will be ignored.", properties.getArgumentType()));
				}
			}
			argumentList.add(argument);
		}
		for (var arg : argumentList) {
			if (parsedArgumentsMap.containsKey(arg.getType())) {
				var argList = parsedArgumentsMap.get(arg.getType());
				//if (! argList.contains(arg))
					argList.add(arg);
			} else {
				var key = arg.getType();
				List<Argument> values = new ArrayList<>();
				values.add(arg);
				parsedArgumentsMap.put(key, values);
			}
		}


		return Optional.of(parsedArgumentsMap);
	}

	private static void recordParsedArgument(ArgumentType type, Argument argument) {

	}

	private void definitionGeneralFormProcessor(String definition) {
		var squareBracketLeft = "[";
		var squareBracketRight = "]";
		var curlyBracesLeft = "{";
		var curlyBracesRight = "}";
		var equal = "=";
		var comma = ",";
		var optionPrefixShort = "-";
		var optionPrefixLong = "--";
		var sharp = "#";
		var regex = "(\\S+)=(\\S+)";

		if (definition == null || definition.startsWith(sharp)) return;

		Stack<Integer> leftParenStack = new Stack<>();
		for (var i = 0; i < definition.length();  i++) {

			Character token = definition.charAt(i);
			String subExpression = "";
		}
	}
}
