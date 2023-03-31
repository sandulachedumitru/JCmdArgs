package com.hardcodacii.jcmdargs.API_connector.controller;

import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.exception.DefinitionArgumentsParserException;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.module.definitions_arguments_parser_module.service.RuleService;
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

	public void defines(String[] args) throws DefinitionArgumentsParserException {
		errorService.emptyErrorsList();

		var definitionsMapOpt = cmdLineDefinitionParserService.parseDefinitionFile();
		if (definitionsMapOpt.isEmpty()) {
			errorService.displayErrors();
			throw new DefinitionArgumentsParserException("Parser service error");
		}
		var definitionsMap = definitionsMapOpt.get();

		var rulesOfDefinitionOpt = ruleService.applyRules(definitionsMap);
		if (rulesOfDefinitionOpt.isEmpty()) {
			errorService.displayErrors();
			throw new DefinitionArgumentsParserException("Rules service error");
		}
		var rulesOfDefinition = rulesOfDefinitionOpt.get();

		if (errorService.getErrors().size() > 0) {
			errorService.displayErrors();
			throw new DefinitionArgumentsParserException("Defining command line failed with errors");
		}
	}
}
