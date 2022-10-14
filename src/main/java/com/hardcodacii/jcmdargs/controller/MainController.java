package com.hardcodacii.jcmdargs.controller;

import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.LogProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

/**
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
	private final SystemEnvironmentVariable environment;
	private final LogProcessorService logProcessorService;
	public void start(String[] args) {
		log.info("This is my message");
		log.info(environment.SERVICE_DISPLAY_MESSAGE_FILEEXIST_FILEEXIST, "MitiFile.txttxtxtxt");
		log.info(environment.SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFOLDER, "Miti folder");

		logProcessorService.processLogs(environment.SERVICE_DISPLAY_MESSAGE_FILEEXIST_FILEEXIST, "Miti File", "Miti folder");
		logProcessorService.processLogs(environment.SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFOLDER, "Miti File", "Miti folder");
		logProcessorService.processLogs(environment.SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFOLDER, "Miti File", "Miti folder", "Miti any");
		logProcessorService.processLogs(environment.SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFOLDER, "Miti File");
		logProcessorService.processLogs(environment.SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFOLDER);
	}
}
