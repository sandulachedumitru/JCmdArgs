package com.hardcodacii.jcmdargs.service.imp;


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
		System.out.println(obj);
		logChacheService.showln(obj);
	}

	@Override
	public void show(Object obj) {
		System.out.print(obj);
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
