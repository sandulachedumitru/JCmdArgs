package com.hardcodacii.jcmdargs.service.impl;


import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.DisplayService;
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

	private final String CRLF = environment.LOG_PARAGRAPH_CRLF;
	private final String INFO = environment.LOG_INFO_PREFIX;
	private final String ERROR = environment.LOG_ERROR_PREFIX;
	private final String WARNING = environment.LOG_WARNING_PREFIX;

	private boolean isNewLine = true;

	@Override
	public void infoLn(Object infoLog, Object... args) {
		printLn(INFO, infoLog, args);
	}

	@Override
	public void info(Object infoLog, Object... args) {
		print(INFO, infoLog, args);
	}

	@Override
	public void errorLn(Object errorLog, Object... args) {
		printLn(ERROR, errorLog, args);
	}

	@Override
	public void error(Object errorLog, Object... args) {
		print(ERROR, errorLog, args);
	}

	@Override
	public void warningLn(Object warningLog, Object... args) {
		printLn(WARNING, warningLog, args);
	}

	@Override
	public void warning(Object warningLog, Object... args) {
		print(WARNING, warningLog, args);
	}

	private void printLn(String prefix, Object logObj, Object... args) {
		if (!isNewLine) {
			System.out.println();
			isNewLine = true;
		}
		var log = prefix + logObj.toString();
		System.err.println(processLogs(log, args));
	}

	private void print(String prefix, Object logObj, Object... args) {
		var log = logObj.toString();
		if (isNewLine) {
			log = prefix + log;
			isNewLine = false;
		}
		System.out.print(processLogs(log, args));
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
}
