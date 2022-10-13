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
	@Value("${cmdline.file.default-output-file}")
	public static String FILE_DEFAULT_OUTPUT;

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

	@Value("${cmdline.log.paragraph.crlf}")
	public static String LOG_PARAGRAPH_CRLF;
	@Value("${cmdline.log.paragraph.tab}")
	public static String LOG_PARAGRAPH_TAB;
	@Value("${cmdline.log.error.prefix}")
	public static String LOG_ERROR_PREFIX;
	@Value("${cmdline.log.section.delimiter.minus}")
	public static String LOG_SECTION_DELIMITER_MINUS;
	@Value("${cmdline.log.section.delimiter.equal}")
	public static String LOG_SECTION_DELIMITER_EQUAL;

	@Value("${cmdline.service.display-service.message-with-param.file-exist.file-exist}")
	public static String SERVICE_DISPLAY_MESSAGE_FILEEXIST_FILEEXIST;
	@Value("${cmdline.service.display-service.message-with-param.file-exist.file-not-exist}")
	public static String SERVICE_DISPLAY_MESSAGE_FILEEXIST_FILENOTEXIST;
	@Value("${cmdline.service.display-service.message-with-param.file-exist.is-folder}")
	public static String SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFOLDER;
	@Value("${cmdline.service.display-service.message-with-param.file-exist.is-file}")
	public static String SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFILE;
	@Value("${cmdline.service.display-service.message-with-param.write-to-file.successful}")
	public static String SERVICE_DISPLAY_MESSAGE_WRITETOFILE_SUCCESSFUL;
	@Value("${cmdline.service.display-service.message-with-param.write-to-file.failed}")
	public static String SERVICE_DISPLAY_MESSAGE_WRITETOFILE_FAILED;
	@Value("${cmdline.service.display-service.message-with-param.read-from-file.successful}")
	public static String SERVICE_DISPLAY_MESSAGE_READFROMFILE_SUCCESSFUL;
	@Value("${cmdline.service.display-service.message-with-param.read-from-file.failed}")
	public static String SERVICE_DISPLAY_MESSAGE_READFROMFILE_FAILED;
}
