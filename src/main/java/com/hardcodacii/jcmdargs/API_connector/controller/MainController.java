package com.hardcodacii.jcmdargs.API_connector.controller;

import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.exception.DefinitionArgumentsParserException;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.exception.ResourcesGeneratorException;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.exception.RulesException;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.ResourcesGeneratorService;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.RuleService;
import com.hardcodacii.logsindentation.service.DisplayService;
import com.hardcodacii.logsindentation.service.ErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class MainController {
	private final CmdLineDefinitionParserService cmdLineDefinitionParserService;
	private final RuleService ruleService;
	private final ErrorService errorService;
	private final ResourcesGeneratorService genResService;
	private final DisplayService displayService;

	public void defines(String[] args) throws DefinitionArgumentsParserException {
		errorService.emptyErrorsList();

		displayService.infoLn("DEFINITIONS PARSER MODULE");
		var definitionsMapOpt = cmdLineDefinitionParserService.parseDefinitionFile();
		if (definitionsMapOpt.isEmpty()) {
			errorService.displayErrors();
			throw new DefinitionArgumentsParserException("Parser service error");
		}
		var definitionsMap = definitionsMapOpt.get();

		displayService.infoLn("DEFINITIONS RULES MODULE");
		var rulesOfDefinitionOpt = ruleService.applyRules(definitionsMap);
		if (rulesOfDefinitionOpt.isEmpty() || !rulesOfDefinitionOpt.get()) {
			errorService.displayErrors();
			throw new RulesException("Rules service error");
		}

		displayService.infoLn("RESOURCES GENERATOR MODULE");
		var generatedResourcesOpt = genResService.generateResources((definitionsMap));
		if (generatedResourcesOpt.isEmpty()) {
			errorService.displayErrors();
			throw new ResourcesGeneratorException("Resources generator service error");
		}

		displayService.infoLn("DISPLAY ERRORS");
		errorService.displayErrors();
		if (errorService.getErrors().size() > 0) {
			throw new DefinitionArgumentsParserException("Defining command line failed with errors");
		}
	}
}
