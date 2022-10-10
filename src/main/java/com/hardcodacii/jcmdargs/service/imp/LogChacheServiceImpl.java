package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.service.LogChacheService;
import org.springframework.stereotype.Service;

import static com.hardcodacii.jcmdargs.global.SystemEnvironmentVariable.*;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
public class LogChacheServiceImpl implements LogChacheService {
	private String CRLF = LOG_PARAGRAPH_CRLF;
	private String ERROR = LOG_ERROR_PREFIX;

	private StringBuilder logCache = new StringBuilder();

	public void showln(Object obj) {
		logCache.append(obj.toString());
		logCache.append(CRLF);
	}

	public void show(Object obj) {
		logCache.append(obj.toString());
	}

	public void showlnErr(Object obj) {
		logCache.append(ERROR + obj.toString());
		logCache.append(CRLF);
	}

	public void showErr(Object obj) {
		logCache.append(ERROR + obj.toString());
	}

	public StringBuilder getLogCache() {
		return logCache;
	}

	public String getErrorPrefix() {
		return ERROR;
	}
}
