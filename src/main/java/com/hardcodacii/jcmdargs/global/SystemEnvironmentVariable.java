package com.hardcodacii.jcmdargs.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Component
public class SystemEnvironmentVariable {
	// FILE

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

	// PROPERTIES

	@Value("${cmdline.properties.global.app-name}")
	public String APP_NAME;

	// REGEX

	@Value("${cmdline.regex.definition.line.argument-definition-simple}")
	public String REGEX_DEFINITION_LINE_ARGUMENT_SIMPLE;
	@Value("${cmdline.regex.definition.line.argument-definition-with-values}")
	public String REGEX_DEFINITION_LINE_ARGUMENT_WITH_VALUES;

	@Value("${cmdline.regex.definition.line.argument-definition-mix}")
	public String REGEX_DEFINITION_LINE_ARGUMENT_MIX;
	@Value("${cmdline.regex.option.name.long}")
	public String REGEX_OPTION_LONG;
	@Value("${cmdline.regex.option.name.short}")
	public String REGEX_OPTION_SHORT;
	@Value("${cmdline.regex.parameter.file}")
	public String REGEX_PARAMETER_FILE;
	@Value("${cmdline.regex.parameter.anything}")
	public String REGEX_PARAMETER_ANYTHING;

	// LOGs

	@Value("${cmdline.log.paragraph.crlf}")
	public String LOG_PARAGRAPH_CRLF;
	@Value("${cmdline.log.paragraph.tab}")
	public String LOG_PARAGRAPH_TAB;
	@Value("${cmdline.log.error.prefix}")
	public String LOG_ERROR_PREFIX;
	@Value("${cmdline.log.info.prefix}")
	public String LOG_INFO_PREFIX;
	@Value("${cmdline.log.section.delimiter.minus}")
	public String LOG_SECTION_DELIMITER_MINUS;
	@Value("${cmdline.log.section.delimiter.equal}")
	public String LOG_SECTION_DELIMITER_EQUAL;

	// TOKENIZE ARGUMENT DEFINITION

	@Value("${cmdline.tokenize-argument-definition.global.empty-text}")
	public String TOKEN_GLOBAL_EMPTY_TEXT;

	@Value("${cmdline.tokenize-argument-definition.special-character.square-bracket-left}")
	public Character TOKEN_SPECIAL_CHAR_SQUARE_BRACKET_LEFT;
	@Value("${cmdline.tokenize-argument-definition.special-character.square-bracket-right}")
	public Character TOKEN_SPECIAL_CHAR_SQUARE_BRACKET_RIGHT;
	@Value("${cmdline.tokenize-argument-definition.special-character.curly-braces-left}")
	public Character TOKEN_SPECIAL_CHAR_CURLY_BRACES_LEFT;
	@Value("${cmdline.tokenize-argument-definition.special-character.curly-braces-right}")
	public Character TOKEN_SPECIAL_CHAR_CURLY_BRACES_RIGHT;
	@Value("${cmdline.tokenize-argument-definition.special-character.equal}")
	public Character TOKEN_SPECIAL_CHAR_EQUAL;
	@Value("${cmdline.tokenize-argument-definition.special-character.comma}")
	public Character TOKEN_SPECIAL_CHAR_COMMA;
	@Value("${cmdline.tokenize-argument-definition.special-character.option-prefix-short}")
	public Character TOKEN_SPECIAL_CHAR_OPTION_PREFIX_SHORT;
	@Value("${cmdline.tokenize-argument-definition.special-character.option-prefix-long}")
	public String TOKEN_SPECIAL_CHAR_OPTION_PREFIX_LONG;
	@Value("${cmdline.tokenize-argument-definition.special-character.sharp}")
	public Character TOKEN_SPECIAL_CHAR_SHARP;
	@Value("${cmdline.tokenize-argument-definition.special-character.single-option-allowed}")
	public Character TOKEN_SPECIAL_CHAR_SINGLE_OPTION_ALLOWED;
}
