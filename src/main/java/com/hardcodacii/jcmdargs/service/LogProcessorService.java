package com.hardcodacii.jcmdargs.service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface LogProcessorService {
	String CURLY_BRACES = "\\{\\}";
	String processLogs(String log, String... arg);
}
