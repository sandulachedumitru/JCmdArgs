package com.hardcodacii.jcmdargs.module.definitions_file_parser.service.impl;

import com.hardcodacii.jcmdargs.module.commons.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.module.commons.service.FileIOService;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.service.ResourcesGeneratorService;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionNonOption;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionOption;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionType;
import com.hardcodacii.logsindentation.service.DisplayService;
import com.hardcodacii.logsindentation.service.ErrorService;
import com.hardcodacii.logsindentation.service.model.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class ResourcesGeneratorServiceImpl implements ResourcesGeneratorService {
	private final ErrorService errorService;
	private final DisplayService displayService;
	private final SystemEnvironmentVariable environment;
	private final FileIOService fileIOService;

	@Override
	public Optional<Set<String>> generateResources(String pathToDefinitionFile, Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null || definitionsMap.isEmpty()) {
			errorService.addError(new Error(displayService.errorLn("definitions map [{}] si null or empty", definitionsMap)));
			return Optional.empty();
		}

		// CREATE FOLDER FOR HELP FILES
		var helpFolder = environment.PATH_TO_RESOURCES + environment.PATH_TO_HELP_FILES;
		try {
			var path = Path.of(helpFolder);
			var dir = Files.createDirectories(path);
			displayService.infoLn("Folder is created: [{}]", "\u001B[33m" + fileIOService.toUnixPath(dir.toAbsolutePath()) + "\u001B[0m");
		} catch (IOException e) {
			displayService.infoLn("Failed to create folder: [{}] -> [{}]", helpFolder, e.getMessage());
		}

		// IDENTIFY WHAT HELP FILES SHOULD BE CREATED
		Set<String> helpFileName = new HashSet<>();
		for (var key : definitionsMap.keySet()) {
			if (key == DefinitionType.OPTION) {
				for (var defOpt : definitionsMap.get(key)) {
					var optsDefList = ((DefinitionOption) defOpt).getOptsDefinitions();
					String optShort = "", optLong = "";

					var optPrefixLong = environment.TOKEN_SPECIAL_CHAR_OPTION_PREFIX_LONG;
					var optPrefixShort = environment.TOKEN_SPECIAL_CHAR_OPTION_PREFIX_SHORT.toString();

					for (var opt : optsDefList) {
						if (opt.startsWith(optPrefixLong)) {
							optLong = opt.replaceFirst(optPrefixLong, "");
						} else if (opt.startsWith(optPrefixShort)) {
							optShort = opt.replaceFirst(optPrefixShort, "");
						}
					}
					if (!optLong.trim().equals("")) helpFileName.add(optLong);
					else if (!optShort.trim().equals("")) helpFileName.add(optShort);
				}
			} else if (key == DefinitionType.COMMAND) {
				for (var defCmd : definitionsMap.get(key)) {
					var cmdsDefList = ((DefinitionNonOption) defCmd).getPossibleValues();
					helpFileName.addAll(cmdsDefList);
				}
			}
		}

		// CREATE HELP FILES
		// check if the help file exist in folder, and if yes than skip it (do not override)
		for (var file : helpFileName) {
			var helpFile = helpFolder + file + ".help";
			displayService.infoLn(environment.LOG_SECTION_DELIMITER_MINUS);
			displayService.infoLn(" Check if the [{}] file exist ...", helpFile);
			if (fileIOService.fileExists(helpFile)) {
				displayService.warningLn("Because [{}] exists, it will be skipped", helpFile);
			} else {
				displayService.infoLn(" Create and write to [{}] ...", helpFile);
				fileIOService.writeStringToFile(helpFile, "replace this line with real help for [" + helpFile + "]");
			}
		}

		// COPY DEFINITION fILE INTO HELP FOLDER
		var toFile = environment.PATH_TO_RESOURCES + environment.FILE_DEFINITION;;
		if (pathToDefinitionFile.equals("")) {
			displayService.warningLn("Definitions file [{}] cannot be copied in [{}] folder because the name of origin file is empty", pathToDefinitionFile, toFile);
		}
		fileIOService.copyFile(pathToDefinitionFile, toFile);

		return helpFileName.isEmpty() ? Optional.empty() : Optional.of(helpFileName);
	}
}
