package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.service.LogChacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class LogChacheServiceImpl implements LogChacheService {
	private final SystemEnvironmentVariable environment;
	private String CRLF() {
		return environment.LOG_PARAGRAPH_CRLF;
	}
	private String ERROR() {
		return environment.LOG_ERROR_PREFIX;
	}

	private StringBuilder logCache = new StringBuilder();

	public void showln(Object obj) {
		logCache.append(obj.toString());
		logCache.append(CRLF());
	}

	public void show(Object obj) {
		logCache.append(obj.toString());
	}

	public void showlnErr(Object obj) {
		logCache.append(ERROR() + obj.toString());
		logCache.append(CRLF());
	}

	public void showErr(Object obj) {
		logCache.append(ERROR() + obj.toString());
	}

	public StringBuilder getLogCache() {
		return logCache;
	}

	public String getErrorPrefix() {
		return ERROR();
	}
}
