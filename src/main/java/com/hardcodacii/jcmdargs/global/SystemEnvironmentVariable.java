package com.hardcodacii.jcmdargs.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Component
public class SystemEnvironmentVariable {
	@Value("${cmdline.app-name}")
	public static String APP_NAME;

	@Value("${cmdline.file.path-to-resources}")
	public static String PATH_TO_RESOURCES;
	@Value("${cmdline.file.path-to-help-files}")
	public static String PATH_TO_HELP_FILES;
	@Value("${cmdline.file.definition-file}")
	public static String FILE_DEFINITION;
	@Value("${cmdline.file.default-input-file}")
	public static String FILE_DEFAULT_INPUT;

	@Value("${cmdline.properties.global.option.prefix.name.long}")
	public static String OPTION_LONG_PREFIX;
	@Value("${cmdline.properties.global.option.prefix.name.short}")
	public static String OPTION_SHORT_PREFIX;

	@Value("${cmdline.regex.option.name.long}")
	public static String REGEX_OPTION_LONG;
	@Value("${cmdline.regex.option.name.short}")
	public static String REGEX_OPTION_SHORT;
	@Value("${cmdline.regex.parameter.file}")
	public static String REGEX_PARAMETER_FILE;
	@Value("${cmdline.regex.parameter.anything}")
	public static String REGEX_PARAMETER_ANYTHING;
}
