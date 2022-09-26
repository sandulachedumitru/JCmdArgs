package com.hardcodacii.jcmdargs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class FileIOService {
	private static final String RESULT_FILE = "SudokuResults.txt";

	private final DisplayService displayService;

	public boolean fileExists(String path) {
		boolean fileExists = false;

		Path pathSource = Paths.get(path);
		if (Files.exists(pathSource, LinkOption.NOFOLLOW_LINKS)) {
			displayService.showln("The file/directory \"" + pathSource.getFileName() + "\" exists");
			// check whether it is a file or a directory
			if (Files.isDirectory(pathSource, LinkOption.NOFOLLOW_LINKS)) {
				displayService.showlnErr("\"" + pathSource.getFileName() + "\" is a directory. Need a file.");
				//if directory then teminate app
			} else {
				displayService.showln("\"" + pathSource.getFileName() + "\" is a file");
				fileExists = true;
			}
		} else
			displayService.showlnErr("The file \"" + pathSource.getFileName() + "\" does not exist");

		return fileExists;
	}

	public boolean writeStringToFile(String str) {
		Path filePath = Paths.get(RESULT_FILE);
		boolean isSuccessfulWriting = false;

		try {
			Files.writeString(filePath, str, StandardOpenOption.CREATE);
			isSuccessfulWriting = true;
			displayService.showln("Successful write to file");
		} catch (IOException ioe) {
			displayService.showlnErr("Failed to write to file");
			displayService.show(ioe);
		}

		return isSuccessfulWriting;
	}

	public String readStringFromFile(String path) {
		Path filePath = Paths.get(path);
		String content = null;

		try {
			content = Files.readString(filePath);
			displayService.showln("Successful read from file");
		} catch (IOException ioe) {
			displayService.showlnErr("Failed to read from file");
			displayService.show(ioe);
		}

		return content;
	}
}
