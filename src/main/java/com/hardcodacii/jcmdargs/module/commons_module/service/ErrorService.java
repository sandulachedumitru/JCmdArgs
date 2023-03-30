package com.hardcodacii.jcmdargs.module.commons_module.service;

import com.hardcodacii.jcmdargs.module.commons_module.service.model.Error;

import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface ErrorService {
	void addError(Error error);

	void emptyErrorsList();

	List<Error> getErrors();

	void displayErrors();
}
