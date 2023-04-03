package com.hardcodacii.jcmdargs.module.commons_module.service.impl;

import com.hardcodacii.jcmdargs.module.commons_module.service.FileIOService;
import com.hardcodacii.jcmdargs.module.commons_module.service.model.PathType;
import com.hardcodacii.logsindentation.service.DisplayService;
import com.hardcodacii.logsindentation.service.ErrorService;
import com.hardcodacii.logsindentation.service.model.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class FileIOServiceImpl implements FileIOService {
	private final DisplayService displayService;
	private final ErrorService errorService;

	@Override
	public PathType checkPath(String path) {
		if (path == null || path.trim().equals("")) {
			errorService.addError(new Error(displayService.errorLn("[{}] is null or empty string.", path)));
			return PathType.NOT_EXIST;
		}

		try {
			var filePath = Paths.get(path);
			var filePathStr = filePath.getFileName().toString();
			if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
				// check whether it is a file or a directory
				if (Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS)) {
					displayService.infoLn("\t[{}] is a folder and exists", filePathStr);
					return PathType.FOLDER;
				} else {
					displayService.infoLn("\t[{}] is a file and exists", filePathStr);
					return PathType.FILE;
				}
			} else {
				displayService.infoLn("The file/folder [{}] does not exist.", filePathStr);
				return PathType.NOT_EXIST;
			}
		} catch (Exception e) {
			errorService.addError(new Error(displayService.errorLn("There was an exception when trying to access the resource/path [{}].", path)));
			e.printStackTrace();
			return PathType.NOT_EXIST;
		}
	}

	@Override
	public boolean fileExists(String path) {
		return checkPath(path) == PathType.FILE;
	}

	@Override
	public boolean fileExistsInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return fileExists(resource.getFile().getPath());
		} catch (IOException e) {
			errorService.addError(new Error(displayService.errorLn("There was an exception when trying to access the file [{}].", path)));
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean folderExists(String path) {
		return checkPath(path) == PathType.FOLDER;
	}

	@Override
	public boolean folderExistsInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return folderExists(resource.getFile().getPath());
		} catch (IOException e) {
			errorService.addError(new Error(displayService.errorLn("There was an exception when trying to access the folder [{}].", path)));
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean writeStringToFile(String path, String content) {
		var filePath = Paths.get(path);
		var isSuccessfulWriting = false;

		try {
			Files.writeString(filePath, content, StandardOpenOption.CREATE);
			isSuccessfulWriting = true;
			displayService.infoLn("Successful write to file [{}].", filePath);
		} catch (IOException ioe) {
			errorService.addError(new Error(displayService.errorLn("Failed to write to file [{}].", filePath)));
			displayService.info(ioe);
		}

		return isSuccessfulWriting;
	}

	@Override
	public boolean writeStringToFileInResources(String path, String content) {
		Resource resource = new ClassPathResource(path);
		try {
			return writeStringToFile(resource.getFile().getPath(), content);
		} catch (IOException e) {
			errorService.addError(new Error(displayService.errorLn("There was an exception when trying to access the file [{}].", path)));
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String readStringFromFile(String path) {
		var filePath = Paths.get(path);
		String content = null;

		try {
			content = Files.readString(filePath);
			displayService.infoLn("Successful read from file [{}].", filePath);
		} catch (IOException ioe) {
			errorService.addError(new Error(displayService.errorLn("Failed to read to file [{}].", filePath)));
			displayService.info(ioe);
		}

		return content;
	}

	@Override
	public String readStringFromFileInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return readStringFromFile(resource.getFile().getPath());
		} catch (IOException e) {
			errorService.addError(new Error(displayService.errorLn("There was an exception when trying to access the file [{}].", path)));
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String toUnixPath(Path path) {
		return path.toString().replace("\\", "/");
	}
}
