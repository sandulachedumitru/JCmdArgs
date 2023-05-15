package com.hardcodacii.jcmdargs;

import com.hardcodacii.jcmdargs.API_connector.service.CommandLineService;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.exception.DefinitionArgumentsParserException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;

@SpringBootApplication
@RequiredArgsConstructor
public class JCmdArgsApplication implements CommandLineRunner {
	private final CommandLineService commandLineService;

	public static void main(String[] args) {
		SpringApplication.run(JCmdArgsApplication.class, args);
	}

	@Override
	public void run(String... args) throws DefinitionArgumentsParserException, FileNotFoundException {
		if (args.length == 0) {
			System.out.println("Need a file path as argument");
			return;
		}
		var file = args[0];
		commandLineService.getDefinitionsFromFile(file);
		commandLineService.processParametersFromCmdLine(args);
	}
}
