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
	@Override
	public String processLogs(String log, String... args) {
		int index= 0;
		String log2 = log;
		while(log2.contains("{}")) {
			if (args.length != 0 && index < args.length) {
				log2 = log2.replaceFirst(CURLY_BRACES, args[index]);
			} else break;
			index++;
		}

		System.out.println("replace: " + log2);
		System.out.println("log: " + log);
		System.out.println();

		return null;
	}
}
