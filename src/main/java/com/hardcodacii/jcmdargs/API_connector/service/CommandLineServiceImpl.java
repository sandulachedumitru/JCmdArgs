package com.hardcodacii.jcmdargs.API_connector.service;

import com.hardcodacii.jcmdargs.module.commons.service.FileIOService;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.exception.DefinitionArgumentsParserException;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.exception.ResourcesGeneratorException;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.exception.RulesException;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.service.ResourcesGeneratorService;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.service.RuleService;
import com.hardcodacii.logsindentation.service.DisplayService;
import com.hardcodacii.logsindentation.service.ErrorService;
import com.hardcodacii.logsindentation.service.model.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

/**
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class CommandLineServiceImpl implements CommandLineService {
	private final CmdLineDefinitionParserService cmdLineDefinitionParserService;
	private final RuleService ruleService;
	private final ErrorService errorService;
	private final ResourcesGeneratorService genResService;
	private final DisplayService displayService;
	private final FileIOService fileIOService;

	@Override
	public void getDefinitionsFromFile(String definitionsFile) throws DefinitionArgumentsParserException, FileNotFoundException {
		errorService.emptyErrorsList();

		// CHECKING FILE
		displayService.infoLn("CHECKING FILE");
		displayService.infoLn("============-");
		var fileExists = fileIOService.fileExists(definitionsFile);
		displayService.emptyLine();

		// check if file exists
		displayService.infoLn("CHECK IF FILE EXISTS");
		displayService.infoLn("=============================");
		if (!fileExists) {
			var log = displayService.errorLn("File [{}] doesn't existS.", definitionsFile);
			errorService.addError(new Error(log));
			throw new FileNotFoundException(log);
		}
		displayService.infoLn("File [{}] existS.", definitionsFile);
		displayService.emptyLine();

		// FLOW OF DEFINITIONS
		displayService.emptyLine();
		displayService.infoLn("DEFINITIONS PARSER MODULE");
		var definitionsMapOpt = cmdLineDefinitionParserService.parseDefinitionFile(definitionsFile);
		if (definitionsMapOpt.isEmpty()) {
			errorService.displayErrors();
			throw new DefinitionArgumentsParserException("Parser service error");
		}
		var definitionsMap = definitionsMapOpt.get();

		displayService.emptyLine();
		displayService.infoLn("DEFINITIONS RULES MODULE");
		var rulesOfDefinitionOpt = ruleService.applyRules(definitionsMap);
		if (rulesOfDefinitionOpt.isEmpty() || !rulesOfDefinitionOpt.get()) {
			errorService.displayErrors();
			throw new RulesException("Rules service error");
		}

		displayService.emptyLine();
		displayService.infoLn("RESOURCES GENERATOR MODULE");
		var generatedResourcesOpt = genResService.generateResources(definitionsFile, definitionsMap);
		if (generatedResourcesOpt.isEmpty()) {
			errorService.displayErrors();
			throw new ResourcesGeneratorException("Resources generator service error");
		}

		displayService.emptyLine();
		displayService.infoLn("DISPLAY ERRORS");
		errorService.displayErrors();
		if (errorService.getErrors().size() > 0) {
			throw new DefinitionArgumentsParserException("Defining command line failed with errors");
		}
	}
}
