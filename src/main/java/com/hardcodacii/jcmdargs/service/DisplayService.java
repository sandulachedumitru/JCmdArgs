package com.hardcodacii.jcmdargs.service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface DisplayService {
	void infoLn(Object log);
	void info(Object log);
	void errorLn(Object log);
	void error(Object log);
	void warningLn(Object log);
	void warning(Object log);
}
