package com.hardcodacii.jcmdargs.service.impl;


import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.LogChacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Show logs and results
 *
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class DisplayServiceImpl implements DisplayService {
	private final LogChacheService logChacheService;

	@Override
	public void showln(Object obj) {
		String prefix = logChacheService.getInfoPrefix();
		System.out.println(prefix + obj);
		logChacheService.showln(obj);
	}

	@Override
	public void show(Object obj) {
		String prefix = logChacheService.getInfoPrefix();
		System.out.print(prefix + obj);
		logChacheService.show(obj);
	}

	@Override
	public void showlnErr(Object obj) {
		String prefix = logChacheService.getErrorPrefix();
		System.err.println(prefix + obj);
		logChacheService.showlnErr(obj);
	}

	@Override
	public void showErr(Object obj) {
		String prefix = logChacheService.getErrorPrefix();
		System.err.print(prefix + obj);
		logChacheService.showErr(obj);
	}
}
