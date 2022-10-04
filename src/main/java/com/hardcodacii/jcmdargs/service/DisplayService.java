package com.hardcodacii.jcmdargs.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Show logs and results
 *
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class DisplayService {
	public static final String delimiterMinus = "------------------------------------------------------------------";
	private static final String delimiterEqual = "==================================================================";
	private final LogChacheService logChacheService;
	private boolean SHOW = true;
	private boolean SHOW_DEBUG = false;

	public void showln(Object obj) {
		if (SHOW) {
			System.out.println(obj);
			logChacheService.showln(obj);
		}
	}

	public void show(Object obj) {
		if (SHOW) {
			System.out.print(obj);
			logChacheService.show(obj);
		}
	}

	public void showlnErr(Object obj) {
		if (SHOW) {
			String suffix = logChacheService.getErrorSuffix();
			System.err.println(suffix + obj);
			logChacheService.showlnErr(obj);
		}
	}

	public void showErr(Object obj) {
		if (SHOW) {
			String suffix = logChacheService.getErrorSuffix();
			System.err.print(suffix + obj);
			logChacheService.showErr(obj);
		}
	}

	public void showDebug(Object obj) {
		if (SHOW_DEBUG) showln(obj);
	}

	public void setShow(boolean show) {
		SHOW = show;
	}

	public void setShowDebug(boolean showDebug) {
		SHOW_DEBUG = showDebug;
	}
}
