package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.FileIOService;
import lombok.RequiredArgsConstructor;
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

	@Override
	public boolean fileExists(String path) {
		boolean fileExists = false;

		Path filePath = Paths.get(path);
		if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
			displayService.showln("The file/directory \"" + filePath.getFileName() + "\" exists");
			// check whether it is a file or a directory
			if (Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS)) {
				displayService.showlnErr("\"" + filePath.getFileName() + "\" is a directory. Need a file.");
				//if directory then teminate app
			} else {
				displayService.showln("\"" + filePath.getFileName() + "\" is a file");
				fileExists = true;
			}
		} else
			displayService.showlnErr("The file \"" + filePath.getFileName() + "\" does not exist");

		return fileExists;
	}

	@Override
	public boolean writeStringToFile(String path) {
		Path filePath = Paths.get(path);
		boolean isSuccessfulWriting = false;

		try {
			Files.writeString(filePath, path, StandardOpenOption.CREATE);
			isSuccessfulWriting = true;
			displayService.showln("Successful write to file");
		} catch (IOException ioe) {
			displayService.showlnErr("Failed to write to file");
			displayService.show(ioe);
		}

		return isSuccessfulWriting;
	}

	@Override
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
