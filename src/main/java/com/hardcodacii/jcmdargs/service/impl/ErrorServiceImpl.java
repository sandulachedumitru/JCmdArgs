package com.hardcodacii.jcmdargs.service.impl;

import com.hardcodacii.jcmdargs.service.ErrorService;
import com.hardcodacii.jcmdargs.service.model.Error;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
public class ErrorServiceImpl implements ErrorService {
	private List<Error> errorList = new ArrayList<>();

	@Override
	public void addError(Error error) {
		errorList.add(error);
	}

	@Override
	public void emptyErrorsList() {
		errorList = new ArrayList<>();
	}

	@Override
	public List<Error> getErrors() {
		return errorList;
	}
}
