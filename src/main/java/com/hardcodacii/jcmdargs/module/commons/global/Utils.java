package com.hardcodacii.jcmdargs.module.commons.global;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class Utils {
	public static boolean isStringNullOrEmpty(String str) {
		return str == null || str.equals("");
	}

	public static boolean isArrayNullOrEmpty(String[] strArray) {
		return strArray == null || strArray.length == 0;
	}

	public static String[] sanitizeAllValues(String[] supportedOptions) {
		if (isArrayNullOrEmpty(supportedOptions)) return null;

		String[] sanitizedSupportedOptions = new String[supportedOptions.length];
		int paramCount = 1;
		for (var s = 0; s < supportedOptions.length; s++) {
			if (supportedOptions[s] == null || supportedOptions[s].equals(""))
				sanitizedSupportedOptions[s] = "value" + paramCount++;
			else
				sanitizedSupportedOptions[s] = sanitizeSingleValue(supportedOptions[s]);
		}

		return sanitizedSupportedOptions;
	}

	public static String sanitizeSingleValue(String value) {
		String regex = "[a-zA-Z0-9]+";

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);

		StringBuilder sb = new StringBuilder();
		while (matcher.find())
			sb.append(matcher.group());

		return sb.length() == 0 ? value : sb.toString();
	}
}
