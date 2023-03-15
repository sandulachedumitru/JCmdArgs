package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.controller;

import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.exception.CmdArgsLineException;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.CmdLineDefinitionParserService;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.ErrorService;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.RuleService;
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

	public void defines(String[] args) throws CmdArgsLineException {
		errorService.emptyErrorsList();

		var definitionsMapOpt = cmdLineDefinitionParserService.parseDefinitionFile();
		if (!definitionsMapOpt.isPresent()) {
			errorService.displayErrors();
			throw new CmdArgsLineException("Parser service error");
		}
		var definitionsMap = definitionsMapOpt.get();

		var rulesOfDefinitionOpt = ruleService.applyRules(definitionsMap);
		if (!rulesOfDefinitionOpt.isPresent()) {
			errorService.displayErrors();
			throw new CmdArgsLineException("Rules service error");
		}
		var rulesOfDefinition = rulesOfDefinitionOpt.get();

		if (errorService.getErrors().size() > 0) {
			errorService.displayErrors();
			throw new CmdArgsLineException("Defining command line failed with errors");
		}
	}
}
