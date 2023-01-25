package com.hardcodacii.jcmdargs.service.model;

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
