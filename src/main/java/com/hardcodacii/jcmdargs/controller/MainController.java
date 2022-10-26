package com.hardcodacii.jcmdargs.controller;

import com.hardcodacii.jcmdargs.service.CmdLineDefinitionParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

/**
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Controller
@RequiredArgsConstructor
public class MainController {
	private final CmdLineDefinitionParserService cmdLineDefinitionParserService;
	public void start(String[] args) {
		cmdLineDefinitionParserService.parseArguments();
	}
}
