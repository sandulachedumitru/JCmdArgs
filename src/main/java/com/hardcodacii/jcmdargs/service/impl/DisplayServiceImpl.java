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

	private String CRLF() {
		return environment.LOG_PARAGRAPH_CRLF;
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

	private boolean isNewLine = true;

	@Override
	public void infoLn(Object infoLog) {
		printLn(infoLog, INFO());
	}

	@Override
	public void info(Object infoLog) {
		print(infoLog, INFO());
	}

	@Override
	public void errorLn(Object errorLog) {
		printLn(errorLog, ERROR());
	}

	@Override
	public void error(Object errorLog) {
		print(errorLog, ERROR());
	}

	@Override
	public void warningLn(Object warningLog) {
		printLn(warningLog, WARNING());
	}

	@Override
	public void warning(Object warningLog) {
		print(warningLog, WARNING());
	}

	private void printLn(Object log, String prefix) {
		if (!isNewLine) {
			System.out.println();
			isNewLine = true;
		}
		System.err.println(prefix + log);
	}

	private void print(Object log, String prefix) {
		var _log = log;
		if (isNewLine) {
			_log = prefix + _log;
			isNewLine = false;
		}
		System.out.print(_log);
	}
}
