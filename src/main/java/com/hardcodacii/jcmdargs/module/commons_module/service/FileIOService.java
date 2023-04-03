package com.hardcodacii.jcmdargs.module.commons_module.service;

import com.hardcodacii.jcmdargs.module.commons_module.service.model.PathType;

import java.nio.file.Path;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface FileIOService {
	PathType checkPath(String path);

	boolean fileExists(String path);

	boolean fileExistsInResources(String path);

	boolean folderExists(String path);

	boolean folderExistsInResources(String path);

	boolean writeStringToFile(String path, String content);

	boolean writeStringToFileInResources(String path, String content);

	String readStringFromFile(String path);

	String readStringFromFileInResources(String path);

	String toUnixPath(Path path);
}
