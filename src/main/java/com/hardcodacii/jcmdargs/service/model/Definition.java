package com.hardcodacii.jcmdargs.service.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Getter
@Setter
public abstract class Definition {
	protected DefinitionType type; // ex: OPTION or ARGUMENT
}
