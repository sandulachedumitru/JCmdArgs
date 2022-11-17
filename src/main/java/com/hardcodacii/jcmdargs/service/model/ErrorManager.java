package com.hardcodacii.jcmdargs.service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public class ErrorManager {
	private static List<Error> errorList = new ArrayList<>();

	private ErrorManager() {}

	@Getter
	@Setter
	public static final class Error {
		private String message;
		private int code;
	}

	public static void addError(Error error) {
		errorList.add(error);
	}

	public static void emptyErrorsList() {
		errorList = new ArrayList<>();
	}

	public static List<Error> getErrors() {
		return errorList;
	}
}
