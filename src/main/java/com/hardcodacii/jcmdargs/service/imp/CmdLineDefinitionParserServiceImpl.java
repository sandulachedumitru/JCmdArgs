package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.FileIOService;
import com.hardcodacii.jcmdargs.service.LogProcessorService;
import com.hardcodacii.jcmdargs.service.model.*;
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

			var scanner = new Scanner(fileContent);
			while (scanner.hasNextLine()) {
				var line = scanner.nextLine();
				lineProcessor(line.trim());
			}
			scanner.close();
		}

		for (var entry : parsedArgumentsMap.entrySet()) {
			System.out.println("key: " + entry.getKey() + "\tvalues: " + entry.getValue());
			for (var arg : entry.getValue()) {
				System.out.println("\tvalue: " + arg);

			}
		}

		// ??
		if (parsedArgumentsMap.size() == 0) return Optional.empty();

		parseDefinitionFromArgument();

		return Optional.empty();
	}

	private void lineProcessor(String line) {
		var comment = environmentService.TOKEN_SPECIAL_CHAR_SHARP.toString();
		if (line == null || line.equals("") || line.startsWith(comment)) return;

		var regex = environmentService.REGEX_DEFINITION_LINE_ARGUMENT_MIX;
		var pattern = Pattern.compile(regex);
		var matcher = pattern.matcher(line);
		int NUMBER_OF_GROUPS = 5 + 1;

		displayService.showln(logProcessorService.processLogs("line: {}", line));
		List<Argument> argumentList = new ArrayList<>();
		while (matcher.find()) {
			for (var i = 0; i <= matcher.groupCount(); i++) {
				displayService.showln(logProcessorService.processLogs("\tARGUMENT DEFINITION: group[{}] --> {}", String.valueOf(i), matcher.group(i)));
			}
			if (matcher.groupCount() != NUMBER_OF_GROUPS -1) {
				displayService.showlnErr(logProcessorService.processLogs("The number of matching groups is not good. Expected {} but is actually {}", String.valueOf(NUMBER_OF_GROUPS), String.valueOf(matcher.groupCount())));
				return;
			}

			ArgumentProperties argumentProperties;
			if (matcher.group(NUMBER_OF_GROUPS -2) == null && matcher.group(NUMBER_OF_GROUPS -1) == null) {
				int groupIndex = 0;
				var properties = new ArgumentPropertiesForOption();
				properties.setDefinition(matcher.group(groupIndex++));
				properties.setArgumentType(matcher.group(groupIndex++));
				properties.setOptionDefinition(matcher.group(groupIndex++));
				properties.setOptionAllowedValues(matcher.group(groupIndex++));
				argumentProperties = properties;
			} else {
				int groupIndex = NUMBER_OF_GROUPS;
				var properties = new ArgumentProperties();
				properties.setDefinition(matcher.group(0));
				properties.setOptionAllowedValues(matcher.group(--groupIndex));
				properties.setArgumentType(matcher.group(--groupIndex));
				argumentProperties = properties;
			}

			Argument argument = new Argument();
			var  type = ArgumentType.getArgumentTypeByCode(argumentProperties.getArgumentType());
			if (type != null) {
				argument.setType(type);
				argument.setProperties(argumentProperties);
				argumentList.add(argument);
			} else {
				displayService.showlnErr(logProcessorService.processLogs("The argument type '{}' is unknown and will be ignored.", argumentProperties.getArgumentType()));
			}
		}

		// map argument List to Map
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
	}

	private void parseDefinitionFromArgument() {
		if (parsedArgumentsMap.size() == 0) return;

		for (var entry : parsedArgumentsMap.entrySet()) {
			displayService.showln(logProcessorService.processLogs("key: [{}]", entry.getKey().name()));
			for (var arg : entry.getValue()) {
				displayService.showln(logProcessorService.processLogs("\tvalues: [{}]", arg.toString()));
				if (arg.getType() == ArgumentType.OPTION) {
					if (arg.getProperties() instanceof ArgumentPropertiesForOption) {
						ArgumentPropertiesForOption argumentProperties = (ArgumentPropertiesForOption) arg.getProperties();
						tokenize(argumentProperties.getOptionDefinition());
//						tokenize((argumentProperties.getOptionAllowedValues()));
					} else if (arg.getProperties() instanceof ArgumentProperties) {

					} else {

					}

				} else if (arg.getType() == ArgumentType.COMMAND) {

				} else if (arg.getType() == ArgumentType.ARGUMENT) {

				} else if (arg.getType() == ArgumentType.ARGUMENTS_NUMBER) {

				} else if (arg.getType() == ArgumentType.ALLOWED_ARGUMENTS_ORDER) {

				} else {

				}


			}
		}

	}

	private List<String> tokenize(String properties) {
		var SQUARE_BRACKET_LEFT = environmentService.TOKEN_SPECIAL_CHAR_SQUARE_BRACKET_LEFT;
		var SQUARE_BRACKET_RIGHT = environmentService.TOKEN_SPECIAL_CHAR_SQUARE_BRACKET_RIGHT;
		var CURLY_BRACES_LEFT = environmentService.TOKEN_SPECIAL_CHAR_CURLY_BRACES_LEFT;
		var CURLY_BRACES_RIGHT = environmentService.TOKEN_SPECIAL_CHAR_CURLY_BRACES_RIGHT;
		var EQUAL = environmentService.TOKEN_SPECIAL_CHAR_EQUAL;
		var COMMA = environmentService.TOKEN_SPECIAL_CHAR_COMMA;
		var OPTION_PREFIX_SHORT = environmentService.TOKEN_SPECIAL_CHAR_OPTION_PREFIX_SHORT;
		var OPTION_PREFIX_LONG = environmentService.TOKEN_SPECIAL_CHAR_OPTION_PREFIX_LONG;
		var SHARP = environmentService.TOKEN_SPECIAL_CHAR_SHARP;
		var EMPTY_TEXT = environmentService.TOKEN_GLOBAL_EMPTY_TEXT;
		var SINGLE_OPTION_ALLOWED = environmentService.TOKEN_SPECIAL_CHAR_SINGLE_OPTION_ALLOWED;

		if (properties == null || properties.trim().equals(EMPTY_TEXT)) return null;

		var singleOptionAllowed = properties.startsWith(String.valueOf(SINGLE_OPTION_ALLOWED));
		var start = singleOptionAllowed ? 1 : 0;

		boolean curlyBracesError = false;
		Integer lastIndexLeft = null;
		Integer lastIndexRight = null;
		Integer lastIndexRegular = null;
		int numberOfCurlyBracesLeft = 0;
		int numberOfCurlyBracesRight = 0;

		StringBuilder sb = new StringBuilder();
		List<String> list = new ArrayList<>();

		for (var index = start; index < properties.length(); index++) {
			char token = properties.charAt(index);
			if (token == CURLY_BRACES_LEFT) {
				if (++numberOfCurlyBracesLeft <= 1 && sb.length() == 0) { // 1 left and 1 right curly brace: {...}
					// ok
					lastIndexLeft = index;
				} else {
					curlyBracesError = true;
					break;
				}
			} else if (token == CURLY_BRACES_RIGHT) {
				lastIndexRight = index;
				if (++numberOfCurlyBracesRight <= 1 && lastIndexLeft != null && lastIndexLeft < lastIndexRight && sb.length() != 0) {
					// ok
					list.add(sb.toString());
					sb = new StringBuilder();
				} else {
					curlyBracesError = true;
					break;
				}
			} else if (token == COMMA) {
//				if(lastIndexLeft != null && lastIndexRight != null && lastIndexLeft < index && index < lastIndexRight && sb.length() != 0) {
				if(lastIndexLeft != null && lastIndexLeft < index && sb.length() != 0) {
					// ok
					list.add(sb.toString());
					sb = new StringBuilder();
				} else {
					curlyBracesError = true;
					break;
				}
			} else {
				sb.append(token);
				lastIndexRegular = index;
			}
		}

		if (curlyBracesError) {
			displayService.showlnErr("Issue with curly braces: " + properties);
			return null;
		}

		if ((lastIndexLeft == null && lastIndexRight == null) || (lastIndexLeft != null && lastIndexRight != null && lastIndexLeft < lastIndexRegular && lastIndexRegular < lastIndexRight)) {
			// ok
			if (sb.length() != 0) list.add(sb.toString());
			System.out.println("LIST: " + list);
			return list;
		} else {
			displayService.showlnErr("Issue with curly braces: " + properties);
			return null;
		}
	}

	private List<String> applyRules(List<String> list) {
		// emty list
		return null;
	}
}
