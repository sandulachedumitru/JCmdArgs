package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.service.LogProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class LogProcessorServiceImpl implements LogProcessorService {
	private static final String CURLY_BRACES_REGEX = "\\{}";
	private static final String CURLY_BRACES = "{}";
	@Override
	public String processLogs(String log, String... args) {
		int index= 0;
		String newLog = log;
		while(newLog.contains(CURLY_BRACES)) {
			if (args.length != 0 && index < args.length) {
				newLog = newLog.replaceFirst(CURLY_BRACES_REGEX, args[index]);
			} else break;
			index++;
		}

		return newLog;
	}
}
