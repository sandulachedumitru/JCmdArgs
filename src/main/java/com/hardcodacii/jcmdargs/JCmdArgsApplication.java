package com.hardcodacii.jcmdargs;

import com.hardcodacii.jcmdargs.controller.MainController;
import com.hardcodacii.jcmdargs.exception.CmdArgsLineException;
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
	public void run(String... args) throws CmdArgsLineException {
		mainController.defines(args);
	}
}
