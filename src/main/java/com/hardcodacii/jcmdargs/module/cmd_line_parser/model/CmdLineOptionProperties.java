package com.hardcodacii.jcmdargs.module.cmd_line_parser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Getter
@Setter
@ToString
public class CmdLineOptionProperties {
	private String name; // ex: --debug
	private String value; // ex: enable/disable
}
