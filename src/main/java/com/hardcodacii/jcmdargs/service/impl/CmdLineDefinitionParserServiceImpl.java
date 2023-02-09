package com.hardcodacii.jcmdargs.service.impl;

import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.FileIOService;
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
	private final ErrorServiceImpl errorService;

	private final Map<DefinitionType, List<DefinitionParser>> parsedDefinitionsMap = new HashMap<>();
	private final Map<DefinitionType, List<Definition>> definitionsMap = new HashMap<>();

	@Override
	public Optional<Map<DefinitionType, List<Definition>>> parseDefinitionFile() {
		displayService.emptyLine();

		// CHECKING FILE
		displayService.infoLn("CHECKING FILE");
		displayService.infoLn("============-");
		var file = environmentService.PATH_TO_RESOURCES + environmentService.FILE_DEFINITION;
		var fileExists = fileIOService.fileExistsInResources(file);
		displayService.emptyLine();

		// RULE: CHECK IF FILE EXISTS
		displayService.infoLn("RULE --> CHECK IF FILE EXISTS");
		displayService.infoLn("=============================");
		if (!fileExists) {
			errorService.addError(new Error(displayService.errorLn("File [{}] doesn't existS.", file)));
			displayErrors();
			return Optional.empty();
		}
		displayService.infoLn("File [{}] existS.", file);
		displayService.emptyLine();

		// READING FROM FILE
		displayService.infoLn("READING FROM FILE");
		displayService.infoLn("=================");
		String fileContent = fileIOService.readStringFromFileInResources(file);
		displayService.emptyLine();

		// LINE PROCESSOR
		displayService.infoLn("LINE PROCESSOR");
		displayService.infoLn("==============");
		var scanner = new Scanner(fileContent);
		while (scanner.hasNextLine()) {
			var line = scanner.nextLine();
			lineProcessor(line.trim());
		}
		scanner.close();
		displayService.emptyLine();

		// PARSED DEFINITION MAP
		displayService.infoLn("PARSED DEFINITION MAP");
		displayService.infoLn("=====================");
		for (var entry : parsedDefinitionsMap.entrySet()) {
			displayService.infoLn("key: " + entry.getKey());
			for (var arg : entry.getValue()) {
				displayService.infoLn("\tvalue: " + arg);
			}
		}
		displayService.emptyLine();

		// RULE: CHECK IF DEFINITIONS HAVE BEEN FOUND
		displayService.infoLn("RULE --> CHECK IF DEFINITIONS HAVE BEEN FOUND");
		displayService.infoLn("=============================================");
		if (parsedDefinitionsMap.size() == 0) {
			errorService.addError(new Error(displayService.errorLn("No definition found in file [{}]", file)));
			displayService.emptyLine();
			displayErrors();
			return Optional.empty();
		}
		displayService.infoLn("Definition found in the [{}] file", file);
		displayService.emptyLine();

		// MAPPING OF DEFINITIONS
		parseDefinition();
		displayService.emptyLine();
		displayService.infoLn("DEFINITIONS MAP");
		displayService.infoLn("===============");
		for (var entry : definitionsMap.entrySet()) {
			displayService.infoLn("key: " + entry.getKey());
			for (var arg : entry.getValue()) {
				displayService.infoLn("\tvalue: " + arg);
			}
		}
		displayService.emptyLine();

		// DISPLAY ERRORS AND EMPTY ERRORS STACK
		displayErrors();
		errorService.emptyErrorsList();

		// RETURN
		return errorService.getErrors().size() == 0 ? Optional.of(definitionsMap) : Optional.empty();
	}

	private void displayErrors() {
		displayService.infoLn("ERROR LIST");
		displayService.infoLn("==========");
		for (var err : errorService.getErrors()) {
			displayService.infoLn("\t" + err.getMessage());
		}
		displayService.emptyLine();
	}

	private void lineProcessor(String line) {
		var comment = environmentService.TOKEN_SPECIAL_CHAR_SHARP.toString();
		if (line == null || line.equals("") || line.startsWith(comment)) return;

		var regex = environmentService.REGEX_DEFINITION_LINE_ARGUMENT_MIX;
		var pattern = Pattern.compile(regex);
		var matcher = pattern.matcher(line);
		int NUMBER_OF_GROUPS = 5 + 1;

		displayService.infoLn("line: {}", line);
		List<DefinitionParser> definitionList = new ArrayList<>();
		var isMatched = false;
		while (matcher.find()) {
			for (var i = 0; i <= matcher.groupCount(); i++) {
				displayService.infoLn("\tARGUMENT DEFINITION: group[{}] --> {}", String.valueOf(i), matcher.group(i));
			}
			if (matcher.groupCount() != NUMBER_OF_GROUPS - 1) {
				errorService.addError(new Error(displayService.errorLn("The number of matching groups is not good. Expected [{}] but is actually [{}]", String.valueOf(NUMBER_OF_GROUPS), String.valueOf(matcher.groupCount()))));
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
				isMatched = true;
				definitionParser.setType(type);
				definitionParser.setProperties(definitionProperties);
				definitionList.add(definitionParser);
			} else {
				errorService.addError(new Error(displayService.errorLn("The definitionParser type [{}] is unknown.", definitionProperties.getDefinitionType())));
			}
		}
		if (!isMatched) {
			errorService.addError(new Error(displayService.errorLn("The line [{}] could not be processed.", line)));
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

		List<Definition> definitionList = new ArrayList<>();

		for (var entry : parsedDefinitionsMap.entrySet()) {
			displayService.infoLn("key: [{}]", entry.getKey().name());
			for (var defPropParser : entry.getValue()) {
				displayService.infoLn("\tvalues: [{}]", defPropParser.toString());

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
									errorService.addError(new Error(displayService.errorLn("Character ['!'] not allowed: " + defProp.getAllowedValues())));
								}
//								definition.setSingleOption(transporter.singleOption);
							}
							definitionList.add(definition);
						} else {
							var errMsg = displayService.errorLn("Definition with type OPTION must be of type: [{}]. Found type: [{}]. You have option={opt1, opt2, ..}. Try option={opt1, opt2, ..}={}",
									DefinitionPropertiesParserForOption.class.getSimpleName(),
									defPropParser.getProperties().getClass().getSimpleName());
							errorService.addError(new Error(errMsg));
						}
					} else {
						DefinitionPropertiesParser defProp = defPropParser.getProperties();

						var definition = new DefinitionNonOption();
						definition.setType(defPropParser.getType());
						var transporter = tokenize((defProp.getAllowedValues()));
						if (transporter != null) {
							definition.setPossibleValues(transporter.items);
						}
						definitionList.add(definition);
					}
				}
			}
		}

		// map argument List to Map
		for (var def : definitionList) {
			if (definitionsMap.containsKey(def.getType())) {
				var argList = definitionsMap.get(def.getType());
				argList.add(def);
			} else {
				var key = def.getType();
				List<Definition> values = new ArrayList<>();
				values.add(def);
				definitionsMap.put(key, values);
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
			errorService.addError(new Error(displayService.errorLn("Issue with curly braces: [{}]", properties)));
			return null;
		}

		if ((lastIndexLeft == null && lastIndexRight == null) || (lastIndexLeft != null && lastIndexRight != null && lastIndexLeft < lastIndexRegular && lastIndexRegular <= lastIndexRight)) {
			// ok
			if (sb.length() != 0) list.add(sb.toString());
			displayService.infoLn("List of items: " + list);
			transporter.items = list;

			return transporter;
		} else {
			errorService.addError(new Error(displayService.errorLn("Issue with curly braces: [{}]", properties)));
			return null;
		}
	}

	private static class DefinitionPropertiesTokenizedTransporter {
		private List<String> items;
		private boolean singleOption = false;
	}

	private void applyRules() {
		// CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory

		// CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0

		// CHECK IF arguments_number EXISTS

		// CHECK THE NUMBERS OF ARGUMENTS

		// CHECK IF THE ARGUMENTS HAVE DUPLICATES

		// CHECK IF THE OPTIONS HAVE DUPLICATES

		// CHECK IF THE OPTIONS HAVE DUPLICATES

		// CHECK IF THE COMMANDS HAVE DUPLICATES

	}
}
