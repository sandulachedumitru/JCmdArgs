package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.impl;

import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model.Error;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.DisplayService;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.ErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class ErrorServiceImpl implements ErrorService {
	private final DisplayService displayService;
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

	@Override
	public void displayErrors() {
		displayService.infoLn("ERROR LIST");
		displayService.infoLn("==========");
		if (getErrors().size() > 0) {
			for (var err : getErrors()) {
				displayService.infoLn("\t" + err.getMessage());
			}
		} else displayService.infoLn(("\tNo error detected"));
		displayService.emptyLine();
	}
}
