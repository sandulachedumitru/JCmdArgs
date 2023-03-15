package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.impl;


import com.hardcodacii.jcmdargs.commons_module.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.DisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Show logs and results
 *
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class DisplayServiceImpl implements DisplayService {
	private final SystemEnvironmentVariable environment;

	private boolean isNewLine = true;

	@Override
	public void emptyLine() {
		System.out.println();
	}

	@Override
	public String infoLn(Object infoLog, Object... args) {
		return printLn(INFO(), infoLog, args);
	}

	@Override
	public String info(Object infoLog, Object... args) {
		return print(INFO(), infoLog, args);
	}

	@Override
	public String errorLn(Object errorLog, Object... args) {
		return printLn(ERROR(), errorLog, args);
	}

	@Override
	public String error(Object errorLog, Object... args) {
		return print(ERROR(), errorLog, args);
	}

	@Override
	public String warningLn(Object warningLog, Object... args) {
		return printLn(WARNING(), warningLog, args);
	}

	@Override
	public String warning(Object warningLog, Object... args) {
		return print(WARNING(), warningLog, args);
	}

	private String printLn(String prefix, Object logObj, Object... args) {
		if (!isNewLine) {
			System.out.println();
			isNewLine = true;
		}
		var log = prefix + logObj.toString();
		var processedLog = processLogs(log, args);
		System.out.println(processedLog);
		return processedLog;
	}

	private String print(String prefix, Object logObj, Object... args) {
		var log = logObj.toString();
		if (isNewLine) {
			log = prefix + log;
			isNewLine = false;
		}
		var processedLog = processLogs(log, args);
		System.out.print(processedLog);
		return processedLog;
	}

	private String processLogs(String log, Object... args) {
		var CURLY_BRACES_REGEX = "\\{}";
		var CURLY_BRACES = "{}";

		int index= 0;
		String newLog = log;
		while(newLog.contains(CURLY_BRACES)) {
			if (args.length != 0 && index < args.length) {
				var arg = args[index] != null ? args[index].toString() : "null";
				newLog = newLog.replaceFirst(CURLY_BRACES_REGEX, arg);
			} else break;
			index++;
		}

		return newLog;
	}

	private String ERROR() {
		return environment.LOG_ERROR_PREFIX;
	}

	private String INFO() {
		return environment.LOG_INFO_PREFIX;
	}

	private String WARNING() {
		return environment.LOG_WARNING_PREFIX;
	}
}
