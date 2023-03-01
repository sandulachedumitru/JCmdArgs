package com.hardcodacii.jcmdargs.service;

import java.util.List;

import com.hardcodacii.jcmdargs.service.model.Error;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface ErrorService {
	void addError(Error error);
	void emptyErrorsList();
	List<Error> getErrors();
	void displayErrors();
}
