package com.hardcodacii.jcmdargs;

import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.controller.MainController;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.exception.DefinitionArgumentsParserException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class JCmdArgsApplication implements CommandLineRunner {
	private final MainController mainController;

	public static void main(String[] args) {
		SpringApplication.run(JCmdArgsApplication.class, args);
	}

	@Override
	public void run(String... args) throws DefinitionArgumentsParserException {
		mainController.defines(args);
	}
}
