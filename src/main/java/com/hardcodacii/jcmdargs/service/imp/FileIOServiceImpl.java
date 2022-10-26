package com.hardcodacii.jcmdargs.service.imp;

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
			displayService.showln(logProcessorService.processLogs("The file/directory '{}' exists.", filePathStr));
			// check whether it is a file or a directory
			if (Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS)) {
				displayService.showlnErr(logProcessorService.processLogs("'{}' is a directory. Need a file.", filePathStr));
				//if directory then stop app
			} else {
				displayService.showln(logProcessorService.processLogs("'{}' is a file.", filePathStr));
				fileExists = true;
			}
		} else
			displayService.showlnErr(logProcessorService.processLogs("The file '{}' does not exist.", filePathStr));

		return fileExists;
	}

	@Override
	public boolean fileExistsInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return fileExists(resource.getFile().getPath());
		} catch (IOException e) {
			displayService.showln(logProcessorService.processLogs("There was an exception when trying to access the file '{}'.", path));
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
			displayService.showln("Successful write to file.");
		} catch (IOException ioe) {
			displayService.showlnErr("Failed to write to file.");
			displayService.show(ioe);
		}

		return isSuccessfulWriting;
	}

	@Override
	public boolean writeStringToFileInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return writeStringToFile(resource.getFile().getPath());
		} catch (IOException e) {
			displayService.showln(logProcessorService.processLogs("There was an exception when trying to access the file '{}'.", path));
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
			displayService.showln("Successful read from file.");
		} catch (IOException ioe) {
			displayService.showlnErr("Failed to read from file.");
			displayService.show(ioe);
		}

		return content;
	}

	@Override
	public String readStringFromFileInResources(String path) {
		Resource resource = new ClassPathResource(path);
		try {
			return readStringFromFile(resource.getFile().getPath());
		} catch (IOException e) {
			displayService.showln(logProcessorService.processLogs("There was an exception when trying to access the file '{}'.", path));
			e.printStackTrace();
			return "";
		}
	}
}
