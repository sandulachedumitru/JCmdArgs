package com.hardcodacii.jcmdargs.module.commons.global;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class StringUtils {
	public static final String alphaLowerCase = "abcdefghijklmnopqrstuvwxyz";
	public static final String  alphaUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String  alpha = alphaUpperCase + alphaLowerCase;
	public static final String  numeric = "0123456789";
	public static final String  alphaNumeric = alpha + numeric;

	public static boolean startWithAlpha(String str) {
		if (str == null || str.trim().equals("")) return false;

		char ch = str.charAt(0);
		return alpha.contains(Character.toString(ch));
	}

	public static boolean startWithAlphaUpperCase(String str) {
		if (str == null || str.trim().equals("")) return false;

		char ch = str.charAt(0);
		return alphaUpperCase.contains(Character.toString(ch));
	}

	public static boolean startWithAlphaLowerCase(String str) {
		if (str == null || str.trim().equals("")) return false;

		char ch = str.charAt(0);
		return alphaLowerCase.contains(Character.toString(ch));
	}

	public static boolean startWithNumeric(String str) {
		if (str == null || str.trim().equals("")) return false;

		char ch = str.charAt(0);
		return numeric.contains(Character.toString(ch));
	}

	public static boolean startWithAlphaNumeric(String str) {
		if (str == null || str.trim().equals("")) return false;

		char ch = str.charAt(0);
		return alphaNumeric.contains(Character.toString(ch));
	}

	public static boolean containsOnlyAlphaNumeric(String str) {
		if (str == null || str.trim().equals("")) return false;

		for (var s = 0; s < str.length(); s++) {
			char ch = str.charAt(s);
			if (! alphaNumeric.contains(Character.toString(ch))) return false;
		}

		return true;
	}
}
