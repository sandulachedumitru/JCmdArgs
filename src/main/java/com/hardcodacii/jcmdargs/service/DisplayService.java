package com.hardcodacii.jcmdargs.service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface DisplayService {
	String infoLn(Object logObj, Object... args);
	String info(Object logObj, Object... args);
	String errorLn(Object logObj, Object... args);
	String error(Object logObj, Object... args);
	String warningLn(Object logObj, Object... args);
	String warning(Object logObj, Object... args);
}
