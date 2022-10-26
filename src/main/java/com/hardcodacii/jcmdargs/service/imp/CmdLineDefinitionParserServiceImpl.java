package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.FileIOService;
import com.hardcodacii.jcmdargs.service.model.Argument;
import com.hardcodacii.jcmdargs.service.CmdLineDefinitionParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class CmdLineDefinitionParserServiceImpl implements CmdLineDefinitionParserService {
	private final FileIOService fileIOService;
	private final SystemEnvironmentVariable environmentService;

	@Override
	public Optional<Map<String, Argument>> parseArguments() {
		var file = environmentService.PATH_TO_RESOURCES + environmentService.FILE_DEFINITION;
		var fileExists = fileIOService.fileExistsInResources(file);
		System.out.println("file: " + file + "\tfileExists: " + fileExists);

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

		return Optional.ofNullable(null);
	}

	private static String lineProcessor(String line) {
		var sharp = "#";
		if (line == null || line.equals("") || line.startsWith(sharp)) return "";

		var regexArgumentDefinition = "(\\S+)=(\\S+)";
		var regexArgumentDefinitionWithValues = "(\\S+)=(\\S+)=(\\S+)";
		var regex = "(\\S+)=(\\S+)=(\\S+)|(\\S+)=(\\S+)";

		var patternArgumentDefinition = Pattern.compile(regexArgumentDefinition);
		var patternArgumentDefinitionWithValues = Pattern.compile(regexArgumentDefinitionWithValues);
		var pattern = Pattern.compile(regex);

		var matcherArgumentDefinition = patternArgumentDefinition.matcher(line);
		var matcherArgumentDefinitionWithValues = patternArgumentDefinitionWithValues.matcher(line);
		var matcher = pattern.matcher(line);

//		if (matcherArgumentDefinitionWithValues.find()) {
//			for (var i = 0; i <= matcherArgumentDefinitionWithValues.groupCount(); i++) {
//				System.out.println("\t\tARGUMENT DEFINITION: group[" + i + "]--> " + matcherArgumentDefinitionWithValues.group(i));
//			}
//		} else if (matcherArgumentDefinition.find()) {
//			for (var i = 0; i <= matcherArgumentDefinition.groupCount(); i++) {
//				System.out.println("\t\tARGUMENT DEFINITION: group[" + i + "]--> " + matcherArgumentDefinition.group(i));
//			}
//		}

//		boolean flag = true;
//		while (matcherArgumentDefinitionWithValues.find()) {
//			for (var i = 0; i <= matcherArgumentDefinitionWithValues.groupCount(); i++) {
//				System.out.println("\t\tARGUMENT DEFINITION: group[" + i + "]--> " + matcherArgumentDefinitionWithValues.group(i));
//			}
//			flag = false;
//		}
//		while (flag && matcherArgumentDefinition.find()) {
//			for (var i = 0; i <= matcherArgumentDefinition.groupCount(); i++) {
//				System.out.println("\t\tARGUMENT DEFINITION: group[" + i + "]--> " + matcherArgumentDefinition.group(i));
//			}
//		}

		while (matcher.find()) {
			for (var i = 0; i <= matcher.groupCount(); i++) {
				System.out.println("\t\tARGUMENT DEFINITION: group[" + i + "]--> " + matcher.group(i));
			}
		}

		return "";
	}

	private static void definitionGeneralFormProcessor(String definition) {
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
