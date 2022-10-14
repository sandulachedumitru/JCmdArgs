package com.hardcodacii.jcmdargs.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Component
public class SystemEnvironmentVariable {
	@Value("${cmdline.app-name}")
	public String APP_NAME;

	@Value("${cmdline.file.path-to-resources}")
	public String PATH_TO_RESOURCES;
	@Value("${cmdline.file.path-to-help-files}")
	public String PATH_TO_HELP_FILES;
	@Value("${cmdline.file.definition-file}")
	public String FILE_DEFINITION;
	@Value("${cmdline.file.default-input-file}")
	public String FILE_DEFAULT_INPUT;
	@Value("${cmdline.file.default-output-file}")
	public String FILE_DEFAULT_OUTPUT;

	@Value("${cmdline.properties.global.option.prefix.name.long}")
	public String OPTION_LONG_PREFIX;
	@Value("${cmdline.properties.global.option.prefix.name.short}")
	public String OPTION_SHORT_PREFIX;

	@Value("${cmdline.regex.option.name.long}")
	public String REGEX_OPTION_LONG;
	@Value("${cmdline.regex.option.name.short}")
	public String REGEX_OPTION_SHORT;
	@Value("${cmdline.regex.parameter.file}")
	public String REGEX_PARAMETER_FILE;
	@Value("${cmdline.regex.parameter.anything}")
	public String REGEX_PARAMETER_ANYTHING;

	@Value("${cmdline.log.paragraph.crlf}")
	public String LOG_PARAGRAPH_CRLF;
	@Value("${cmdline.log.paragraph.tab}")
	public String LOG_PARAGRAPH_TAB;
	@Value("${cmdline.log.error.prefix}")
	public String LOG_ERROR_PREFIX;
	@Value("${cmdline.log.section.delimiter.minus}")
	public String LOG_SECTION_DELIMITER_MINUS;
	@Value("${cmdline.log.section.delimiter.equal}")
	public String LOG_SECTION_DELIMITER_EQUAL;

	@Value("${cmdline.service.display-service.message-with-param.file-exist.file-exist}")
	public String SERVICE_DISPLAY_MESSAGE_FILEEXIST_FILEEXIST;
	@Value("${cmdline.service.display-service.message-with-param.file-exist.file-not-exist}")
	public String SERVICE_DISPLAY_MESSAGE_FILEEXIST_FILENOTEXIST;
	@Value("${cmdline.service.display-service.message-with-param.file-exist.is-folder}")
	public String SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFOLDER;
	@Value("${cmdline.service.display-service.message-with-param.file-exist.is-file}")
	public String SERVICE_DISPLAY_MESSAGE_FILEEXIST_ISFILE;
	@Value("${cmdline.service.display-service.message-with-param.write-to-file.successful}")
	public String SERVICE_DISPLAY_MESSAGE_WRITETOFILE_SUCCESSFUL;
	@Value("${cmdline.service.display-service.message-with-param.write-to-file.failed}")
	public String SERVICE_DISPLAY_MESSAGE_WRITETOFILE_FAILED;
	@Value("${cmdline.service.display-service.message-with-param.read-from-file.successful}")
	public String SERVICE_DISPLAY_MESSAGE_READFROMFILE_SUCCESSFUL;
	@Value("${cmdline.service.display-service.message-with-param.read-from-file.failed}")
	public String SERVICE_DISPLAY_MESSAGE_READFROMFILE_FAILED;
}
