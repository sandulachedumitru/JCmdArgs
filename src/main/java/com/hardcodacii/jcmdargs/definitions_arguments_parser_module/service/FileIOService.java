package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface FileIOService {
	boolean fileExists(String path);
	boolean fileExistsInResources(String path);
	boolean writeStringToFile(String str);
	boolean writeStringToFileInResources(String str);
	String readStringFromFile(String path);
	String readStringFromFileInResources(String path);
}
