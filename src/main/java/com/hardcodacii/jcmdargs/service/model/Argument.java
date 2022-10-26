package com.hardcodacii.jcmdargs.service.model;

import lombok.Data;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
public class Argument {
	private ArgumentType type;
	private ArgumentProperties properties;
}
