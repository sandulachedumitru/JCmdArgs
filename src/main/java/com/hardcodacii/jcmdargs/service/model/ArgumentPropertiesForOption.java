package com.hardcodacii.jcmdargs.service.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
@ToString(callSuper = true)
public class ArgumentPropertiesForOption extends ArgumentProperties {
	String optionDefinition; // ex: {--help,-h}
}
