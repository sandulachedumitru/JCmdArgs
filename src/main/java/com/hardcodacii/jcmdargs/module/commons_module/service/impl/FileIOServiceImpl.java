package com.hardcodacii.jcmdargs.module.commons_module.service.impl;

import com.hardcodacii.jcmdargs.module.commons_module.service.DisplayService;
import com.hardcodacii.jcmdargs.module.commons_module.service.ErrorService;
import com.hardcodacii.jcmdargs.module.commons_module.service.FileIOService;
import com.hardcodacii.jcmdargs.module.commons_module.service.model.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class FileIOServiceImpl implements FileIOService {
	private final DisplayService displayService;
	private final ErrorService errorService;

	@Override
	public boolean fileExists(String path) {
		boolean fileExists = false;

		var filePath = Paths.get(path);
		var filePathStr = filePath.getFileName().toString();
		if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
			displayService.infoLn("The file/directory [{}] exists.", filePathStr);
			// check whether it is a file or a directory
			if (Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS)) {
				errorService.addError(new Error(displayService.errorLn("[{}] is a directory. Need a file.", filePathStr)));
				//if directory then stop app
			} else {
				displayService.infoLn("[{}] is a file.", filePathStr);
				fileExists = true;
			}
		} else
			errorService.addError(new Error(displayService.errorLn("The file [{}] does not exist.", filePathStr)));

		return fileExists;
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
	public boolean writeStringToFile(String path) {
		var filePath = Paths.get(path);
		var isSuccessfulWriting = false;

		try {
			Files.writeString(filePath, path, StandardOpenOption.CREATE);
			isSuccessfulWriting = true;
			displayService.infoLn("Successful write to file [{}].", filePath);
		} catch (IOException ioe) {
			errorService.addError(new Error(displayService.errorLn("Failed to write to file [{}].", filePath)));
			displayService.info(ioe);
		}

		return isSuccessfulWriting;
	}

	@Override
	public boolean writeStringToFileInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return writeStringToFile(resource.getFile().getPath());
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
}
