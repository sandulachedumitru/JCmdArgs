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

	private final Map<DefinitionType, List<DefinitionParser>> parsedDefinitionsMap = new HashMap<>();

	private final Map<DefinitionType, Definition> definitionsMap = new HashMap<>();

	@Override
	public Optional<Map<DefinitionType, Definition>> parseDefinitionFile() {
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

		for (var entry : parsedDefinitionsMap.entrySet()) {
			System.out.println("key: " + entry.getKey());
			for (var arg : entry.getValue()) {
				System.out.println("\tvalue: " + arg);
			}
		}

		// ??
		if (parsedDefinitionsMap.size() == 0) return Optional.empty();

		parseDefinition();

		for (var entry : definitionsMap.entrySet()) {
			System.out.println("key: " + entry.getKey() + "\t\t\t\t\tvalue: " + entry.getValue());
		}



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
		List<DefinitionParser> definitionList = new ArrayList<>();
		while (matcher.find()) {
			for (var i = 0; i <= matcher.groupCount(); i++) {
				displayService.showln(logProcessorService.processLogs("\tARGUMENT DEFINITION: group[{}] --> {}", String.valueOf(i), matcher.group(i)));
			}
			if (matcher.groupCount() != NUMBER_OF_GROUPS - 1) {
				displayService.showlnErr(logProcessorService.processLogs("The number of matching groups is not good. Expected {} but is actually {}", String.valueOf(NUMBER_OF_GROUPS), String.valueOf(matcher.groupCount())));
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
				displayService.showlnErr(logProcessorService.processLogs("The definitionParser type '{}' is unknown and will be ignored.", definitionProperties.getDefinitionType()));
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
		if (parsedDefinitionsMap.size() == 0) return;

		for (var entry : parsedDefinitionsMap.entrySet()) {
			displayService.showln(logProcessorService.processLogs("key: [{}]", entry.getKey().name()));
			for (var defParser : entry.getValue()) {
				displayService.showln(logProcessorService.processLogs("\tvalues: [{}]", defParser.toString()));

				if (defParser.getType() != null && defParser.getProperties() != null) {
					var definition = new Definition();
					if (defParser.getType() == DefinitionType.OPTION && defParser.getProperties() instanceof DefinitionPropertiesParserForOption) {
						DefinitionPropertiesParserForOption defProp = (DefinitionPropertiesParserForOption) defParser.getProperties();
						definition.setOptsDefinitions(tokenize(defProp.getOption()));
						definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
					} else {
						DefinitionPropertiesParser defProp = defParser.getProperties();
						definition.setOptsDefinitions(tokenize(defProp.getDefinitionType()));
						definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
					}
					definitionsMap.put(defParser.getType(), definition);

//					if (defParser.getType() == DefinitionType.OPTION) {
//						Definition definition = new Definition();
//						if (defParser.getProperties() instanceof DefinitionPropertiesParserForOption) {
//							DefinitionPropertiesParserForOption defProp = (DefinitionPropertiesParserForOption) defParser.getProperties();
//							definition.setOptsDefinitions(tokenize(defProp.getOption()));
//							definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
//						} else { // defParser.getProperties() is a DefinitionPropertiesParser
//							DefinitionPropertiesParser defProp = defParser.getProperties();
//							definition.setOptsDefinitions(tokenize(defProp.getDefinitionType()));
//							definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
//						}
//						definitionsMap.put(DefinitionType.OPTION, definition);
//					} else if (defParser.getType() == DefinitionType.COMMAND) {
//						DefinitionPropertiesParser defProp = defParser.getProperties();
//						Definition definition = new Definition();
//						definition.setOptsDefinitions(tokenize(defProp.getDefinitionType()));
//						definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
//						definitionsMap.put(DefinitionType.OPTION, definition);
//					} else if (defParser.getType() == DefinitionType.ARGUMENT) {
//						DefinitionPropertiesParser defProp = defParser.getProperties();
//						Definition definition = new Definition();
//						definition.setOptsDefinitions(tokenize(defProp.getDefinitionType()));
//						definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
//						definitionsMap.put(DefinitionType.OPTION, definition);
//					} else if (defParser.getType() == DefinitionType.ARGUMENTS_NUMBER) {
//						DefinitionPropertiesParser defProp = defParser.getProperties();
//						Definition definition = new Definition();
//						definition.setOptsDefinitions(tokenize(defProp.getDefinitionType()));
//						definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
//						definitionsMap.put(DefinitionType.OPTION, definition);
//					} else if (defParser.getType() == DefinitionType.ALLOWED_ARGUMENTS_ORDER) {
//						DefinitionPropertiesParser defProp = defParser.getProperties();
//						Definition definition = new Definition();
//						definition.setOptsDefinitions(tokenize(defProp.getDefinitionType()));
//						definition.setAllowedValues(tokenize((defProp.getAllowedValues())));
//						definitionsMap.put(DefinitionType.OPTION, definition);
//					} else {
//						// error: the type is not registered
//					}
				} else {
					// defParser's type/properties is null
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
