package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Data
@NoArgsConstructor
public class Error {
	private String message;
	private int code = 0;

	public Error(String message) {
		this.message = message;
	}

	public Error(String message, int code) {
		this.message = message;
		this.code = code;
	}
}
