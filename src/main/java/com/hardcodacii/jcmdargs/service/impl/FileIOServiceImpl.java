package com.hardcodacii.jcmdargs.service.impl;

import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.FileIOService;
import com.hardcodacii.jcmdargs.service.LogProcessorService;
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
	private final LogProcessorService logProcessorService;

	@Override
	public boolean fileExists(String path) {
		boolean fileExists = false;

		var filePath = Paths.get(path);
		var filePathStr = filePath.getFileName().toString();
		if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
			displayService.println(logProcessorService.processLogs("The file/directory '{}' exists.", filePathStr));
			// check whether it is a file or a directory
			if (Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS)) {
				displayService.printlnErr(logProcessorService.processLogs("'{}' is a directory. Need a file.", filePathStr));
				//if directory then stop app
			} else {
				displayService.println(logProcessorService.processLogs("'{}' is a file.", filePathStr));
				fileExists = true;
			}
		} else
			displayService.printlnErr(logProcessorService.processLogs("The file '{}' does not exist.", filePathStr));

		return fileExists;
	}

	@Override
	public boolean fileExistsInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return fileExists(resource.getFile().getPath());
		} catch (IOException e) {
			displayService.println(logProcessorService.processLogs("There was an exception when trying to access the file '{}'.", path));
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
			displayService.println("Successful write to file.");
		} catch (IOException ioe) {
			displayService.printlnErr("Failed to write to file.");
			displayService.print(ioe);
		}

		return isSuccessfulWriting;
	}

	@Override
	public boolean writeStringToFileInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return writeStringToFile(resource.getFile().getPath());
		} catch (IOException e) {
			displayService.println(logProcessorService.processLogs("There was an exception when trying to access the file '{}'.", path));
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
			displayService.println("Successful read from file.");
		} catch (IOException ioe) {
			displayService.printlnErr("Failed to read from file.");
			displayService.print(ioe);
		}

		return content;
	}

	@Override
	public String readStringFromFileInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return readStringFromFile(resource.getFile().getPath());
		} catch (IOException e) {
			displayService.println(logProcessorService.processLogs("There was an exception when trying to access the file '{}'.", path));
			e.printStackTrace();
			return "";
		}
	}
}
