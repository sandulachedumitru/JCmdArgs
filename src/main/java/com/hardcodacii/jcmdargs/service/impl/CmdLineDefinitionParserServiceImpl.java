package com.hardcodacii.jcmdargs.service.impl;

import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.FileIOService;
import com.hardcodacii.jcmdargs.service.LogProcessorService;
import com.hardcodacii.jcmdargs.service.model.*;
import com.hardcodacii.jcmdargs.service.model.Error;
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
	private final ErrorServiceImpl errorService;

	private final Map<DefinitionType, List<DefinitionParser>> parsedDefinitionsMap = new HashMap<>();

	private final Map<DefinitionType, Definition> definitionsMap = new HashMap<>();

	@Override
	public Optional<Map<DefinitionType, DefinitionOption>> parseDefinitionFile() {
		var file = environmentService.PATH_TO_RESOURCES + environmentService.FILE_DEFINITION;
		var fileExists = fileIOService.fileExistsInResources(file);

		if (fileExists) {
			String fileContent = fileIOService.readStringFromFileInResources(file);

			System.out.println("\nLINE PROCESSOR");
			System.out.println("==============");
			var scanner = new Scanner(fileContent);
			while (scanner.hasNextLine()) {
				var line = scanner.nextLine();
				lineProcessor(line.trim());
			}
			System.out.println();
			scanner.close();
		}

		System.out.println("\nPARSED DEFINITION MAP");
		System.out.println("=====================");
		for (var entry : parsedDefinitionsMap.entrySet()) {
			System.out.println("key: " + entry.getKey());
			for (var arg : entry.getValue()) {
				System.out.println("\tvalue: " + arg);
			}
		}
		System.out.println();

		// ??
		if (parsedDefinitionsMap.size() == 0) return Optional.empty();

		parseDefinition();

		System.out.println("\nDEFINITIONS MAP");
		System.out.println("===============");
		for (var entry : definitionsMap.entrySet()) {
			System.out.println("key: " + entry.getKey() + "\t\t\t\t\tvalue: " + entry.getValue());
		}
		System.out.println();

		System.out.println("\nERROR LIST");
		System.out.println("==========");
		for (var err : errorService.getErrors()) {
			System.out.println("\t" + err.getMessage());
		}
		System.out.println();

		errorService.emptyErrorsList();

		return Optional.empty();
	}

	private void lineProcessor(String line) {
		var comment = environmentService.TOKEN_SPECIAL_CHAR_SHARP.toString();
		if (line == null || line.equals("") || line.startsWith(comment)) return;

		var regex = environmentService.REGEX_DEFINITION_LINE_ARGUMENT_MIX;
		var pattern = Pattern.compile(regex);
		var matcher = pattern.matcher(line);
		int NUMBER_OF_GROUPS = 5 + 1;

		displayService.println(logProcessorService.processLogs("line: {}", line));
		List<DefinitionParser> definitionList = new ArrayList<>();
		while (matcher.find()) {
			for (var i = 0; i <= matcher.groupCount(); i++) {
				displayService.println(logProcessorService.processLogs("\tARGUMENT DEFINITION: group[{}] --> {}", String.valueOf(i), matcher.group(i)));
			}
			if (matcher.groupCount() != NUMBER_OF_GROUPS - 1) {
				displayService.printlnErr(logProcessorService.processLogs("The number of matching groups is not good. Expected {} but is actually {}", String.valueOf(NUMBER_OF_GROUPS), String.valueOf(matcher.groupCount())));
				return;
			}

			DefinitionPropertiesParser definitionProperties;
			if (matcher.group(NUMBER_OF_GROUPS - 2) == null && matcher.group(NUMBER_OF_GROUPS - 1) == null) {
				int groupIndex = 0;
				var properties = new DefinitionPropertiesParserForOption();
				properties.setDefinition(matcher.group(groupIndex++));
				properties.setDefinitionType(matcher.group(groupIndex++));
				properties.setOption(matcher.group(groupIndex++));
				properties.setAllowedValues(matcher.group(groupIndex++));
				definitionProperties = properties;
			} else {
				int groupIndex = NUMBER_OF_GROUPS;
				var properties = new DefinitionPropertiesParser();
				properties.setDefinition(matcher.group(0));
				properties.setAllowedValues(matcher.group(--groupIndex));
				properties.setDefinitionType(matcher.group(--groupIndex));
				definitionProperties = properties;
			}

			var definitionParser = new DefinitionParser();
			var type = DefinitionType.getDefinitionTypeByCode(definitionProperties.getDefinitionType());
			if (type != null) {
				definitionParser.setType(type);
				definitionParser.setProperties(definitionProperties);
				definitionList.add(definitionParser);
			} else {
				displayService.printlnErr(logProcessorService.processLogs("The definitionParser type '{}' is unknown and will be ignored.", definitionProperties.getDefinitionType()));
			}
		}

		// map argument List to Map
		for (var def : definitionList) {
			if (parsedDefinitionsMap.containsKey(def.getType())) {
				var argList = parsedDefinitionsMap.get(def.getType());
				argList.add(def);
			} else {
				var key = def.getType();
				List<DefinitionParser> values = new ArrayList<>();
				values.add(def);
				parsedDefinitionsMap.put(key, values);
			}
		}
	}

	private void parseDefinition() {
		if (parsedDefinitionsMap.size() == 0) return; // TODO log and errorService

		for (var entry : parsedDefinitionsMap.entrySet()) {
			displayService.println(logProcessorService.processLogs("key: [{}]", entry.getKey().name()));
			for (var defPropParser : entry.getValue()) {
				displayService.println(logProcessorService.processLogs("\tvalues: [{}]", defPropParser.toString()));

				if (defPropParser.getType() != null && defPropParser.getProperties() != null) {
					if (defPropParser.getType() == DefinitionType.OPTION) {
						if (defPropParser.getProperties() instanceof DefinitionPropertiesParserForOption) {
							var defProp = (DefinitionPropertiesParserForOption) defPropParser.getProperties();

							var definition = new DefinitionOption();
							definition.setType(DefinitionType.OPTION);
							var transporter = tokenize(defProp.getOption());
							if (transporter != null) {
								definition.setOptsDefinitions(transporter.items);
								definition.setSingleOption(transporter.singleOption);
							}
							transporter = tokenize(defProp.getAllowedValues());
							if (transporter != null) {
								definition.setAllowedValues(transporter.items);
								if (transporter.singleOption) {
									displayService.printlnErr("Character ['!'] not allowed: " + defProp.getAllowedValues());
									errorService.addError(new Error("Character ['!'] not allowed" + defProp.getAllowedValues()));
								}
//								definition.setSingleOption(transporter.singleOption);
							}
							definitionsMap.put(defPropParser.getType(), definition);
						} else {
							displayService.printlnErr("Definition with type OPTION must be of type: " + DefinitionPropertiesParserForOption.class.getName());
							errorService.addError(new Error("Definition with type OPTION must be of type: " + DefinitionPropertiesParserForOption.class.getName()));
						}
					} else {
						DefinitionPropertiesParser defProp = defPropParser.getProperties();

						var definition = new DefinitionNonOption();
						definition.setType(defPropParser.getType());
						var transporter = tokenize(defProp.getDefinitionType());
						if (transporter != null) {
							definition.setPossibleValues(transporter.items);
						}
						transporter = tokenize((defProp.getAllowedValues()));
						if (transporter != null) {
							definition.setPossibleValues(transporter.items);
						}
						definitionsMap.put(defPropParser.getType(), definition);
					}
				}
			}
		}
	}

	private DefinitionPropertiesTokenizedTransporter tokenize(String properties) {
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

		var transporter = new DefinitionPropertiesTokenizedTransporter();
		transporter.singleOption = singleOptionAllowed;

		boolean curlyBracesError = false;
		Integer lastIndexLeft = null;
		Integer lastIndexRight = null;
		Integer lastIndexRegular = null;
		int numberOfCurlyBracesLeft = 0;
		int numberOfCurlyBracesRight = 0;

		StringBuilder sb = new StringBuilder();
		List<String> list = new ArrayList<>();

		for (var index = start; index < properties.length(); index++) {
			lastIndexRegular = index;
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
				if (++numberOfCurlyBracesRight <= 1 && lastIndexLeft != null && lastIndexLeft < lastIndexRight) {
					// ok
					list.add(sb.toString());
					sb = new StringBuilder();
				} else {
					curlyBracesError = true;
					break;
				}
			} else if (token == COMMA) {
				if (lastIndexLeft != null && lastIndexLeft < index && sb.length() != 0) {
					// ok
					list.add(sb.toString());
					sb = new StringBuilder();
				} else {
					curlyBracesError = true;
					break;
				}
			} else {
				sb.append(token);
			}
		}

		if (curlyBracesError) {
			displayService.printlnErr("Issue with curly braces: " + properties);
			return null;
		}

		if ((lastIndexLeft == null && lastIndexRight == null) || (lastIndexLeft != null && lastIndexRight != null && lastIndexLeft < lastIndexRegular && lastIndexRegular <= lastIndexRight)) {
			// ok
			System.out.println("LIST: " + list);
			transporter.items = list;

			return transporter;
		} else {
			displayService.printlnErr("Issue with curly braces: " + properties);
			return null;
		}
	}

	private static class DefinitionPropertiesTokenizedTransporter {
		private List<String> items;
		private boolean singleOption = false;
	}

	private List<String> applyRules(List<String> list) {
		// emty list
		return null;
	}
}
