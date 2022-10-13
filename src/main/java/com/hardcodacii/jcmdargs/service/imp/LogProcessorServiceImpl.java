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
	public String processLogs(String log) {
		return null;
	}
}
