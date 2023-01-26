package com.hardcodacii.jcmdargs.service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface DisplayService {
	void infoLn(Object logObj, Object... args);
	void info(Object logObj, Object... args);
	void errorLn(Object logObj, Object... args);
	void error(Object logObj, Object... args);
	void warningLn(Object logObj, Object... args);
	void warning(Object logObj, Object... args);
}
