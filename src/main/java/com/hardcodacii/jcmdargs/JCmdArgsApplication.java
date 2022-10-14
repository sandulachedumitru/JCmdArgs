package com.hardcodacii.jcmdargs;

import com.hardcodacii.jcmdargs.controller.MainController;
import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class JCmdArgsApplication implements CommandLineRunner {
	private final MainController mainController;
	private final SystemEnvironmentVariable environment;

	public static void main(String[] args) {
		SpringApplication.run(JCmdArgsApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info(environment.APP_NAME, "MitiFile");
		mainController.start(args);
	}
}
