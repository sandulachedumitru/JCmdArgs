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
		System.out.println("SPLIT ...");
		System.out.println("CURLY_BRACES: " + CURLY_BRACES);
		var splits = log.split(CURLY_BRACES);
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (var s : splits) {
			System.out.println("s: " + s);
			sb.append(s);
			if (args.length != 0 && index < args.length) {
				sb.append(args[index]);
				System.out.println("[" + index + "]: " + args[index]);
			}

			index++;
			System.out.println();
		}
		System.out.println("sb: " + sb);
		System.out.println("log: " + log);

		System.out.println("REPLACE ...");
		index= 0;
		String log2 = log;
		while(log2.contains("{}")) {
			if (args.length != 0 && index < args.length) {
				log2 = log2.replaceFirst(CURLY_BRACES, args[index]);
			}
			index++;
		}

		System.out.println("replace: " + log2);
		System.out.println("log: " + log);

		return null;
	}
}
