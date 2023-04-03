package com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.impl;

import com.hardcodacii.jcmdargs.module.commons_module.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.module.commons_module.service.FileIOService;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.ResourcesGeneratorService;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.DefinitionNonOption;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.DefinitionOption;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.model.DefinitionType;
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

	private final static boolean FAILED = false;
	private final static boolean SUCCESSFUL = true;

	@Override
	public Optional<Set<String>> generateResources(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null || definitionsMap.isEmpty()) {
			errorService.addError(new Error(displayService.errorLn("definitions map [{}] si null or empty", definitionsMap)));
			return Optional.empty();
		}

		var helpFolder = environment.PATH_TO_RESOURCES + environment.PATH_TO_HELP_FILES;
		try {
			var path = Path.of(helpFolder);
			var dir = Files.createDirectories(path);
			displayService.infoLn("Folder is created: [{}]", "\u001B[33m" + fileIOService.toUnixPath(dir.toAbsolutePath()) + "\u001B[0m");
		} catch (IOException e) {
			displayService.infoLn("Failed to create folder: [{}] -> [{}]", helpFolder, e.getMessage());
		}

		Set<String>  helpFileName = new HashSet<>();
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
					if (! optLong.trim().equals("")) helpFileName.add(optLong);
					else if (! optShort.trim().equals("")) helpFileName.add(optShort);
				}
			} else if (key == DefinitionType.COMMAND) {
				for (var defCmd : definitionsMap.get(key)) {
					var cmdsDefList = ((DefinitionNonOption) defCmd).getPossibleValues();
					helpFileName.addAll(cmdsDefList);
				}
			}
		}
		displayService.infoLn("Created help file for {}", helpFileName);

		return helpFileName.isEmpty() ? Optional.empty() : Optional.of(helpFileName);
	}
}
