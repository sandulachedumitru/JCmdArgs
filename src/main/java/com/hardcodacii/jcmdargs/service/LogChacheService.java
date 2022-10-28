package com.hardcodacii.jcmdargs.service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface LogChacheService {
	void showln(Object obj);

	void show(Object obj);

	void showlnErr(Object obj);

	void showErr(Object obj);

	StringBuilder getLogCache();

	String getErrorPrefix();

	String getInfoPrefix();
}
