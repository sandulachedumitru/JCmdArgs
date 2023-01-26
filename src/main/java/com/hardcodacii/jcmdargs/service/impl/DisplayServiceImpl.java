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

	@Override
	public void println(Object obj) {
		String prefix = INFO();
		System.out.println(prefix + obj);
	}

	@Override
	public void print(Object obj) {
		String prefix = INFO();
		System.out.print(prefix + obj);
	}

	@Override
	public void printlnErr(Object obj) {
		String prefix = ERROR();
		System.err.println(prefix + obj);
	}

	@Override
	public void printErr(Object obj) {
		String prefix = ERROR();
		System.err.print(prefix + obj);
	}
}
