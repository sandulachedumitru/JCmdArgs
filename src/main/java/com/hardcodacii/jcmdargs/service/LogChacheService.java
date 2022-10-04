package com.hardcodacii.jcmdargs.service;

import org.springframework.stereotype.Component;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Component
public class LogChacheService {
	private String CRLF = "\r\n";
	private String ERROR = " -ERROR:";

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

	public String getErrorSuffix() {
		return ERROR;
	}
}
