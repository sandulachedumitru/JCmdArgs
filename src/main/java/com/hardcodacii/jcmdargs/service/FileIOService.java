package com.hardcodacii.jcmdargs.service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface FileIOService {
	boolean fileExists(String path);
	boolean writeStringToFile(String str);
	String readStringFromFile(String path);
}
