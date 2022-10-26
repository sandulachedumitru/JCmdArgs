package com.hardcodacii.jcmdargs.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@SpringBootTest
class LogProcessorServiceTest {
	@Autowired
	private LogProcessorService service;

	@Test
	@DisplayName("log with one parameter received a single vararg")
	public void logProcessorWithOneVararg() {
		var log = "This {} is an example.";
		String[] varArgs = {"test"};

		var logPrecessed = service.processLogs(log, varArgs);

		Assertions.assertEquals(logPrecessed, "This test is an example.");
	}

	@Test
	@DisplayName("log with one parameter received a multiple vararg, but extra argument are ignored")
	public void logProcessorWithMultipleVararg() {
		var log = "This {} is an example.";
		String[] varArgs = {"test1", "test2"};

		var logPrecessed = service.processLogs(log, varArgs);

		Assertions.assertEquals(logPrecessed, "This test1 is an example.");
	}

	@Test
	@DisplayName("log with one parameter does not receive any vararg")
	public void logProcessorWithMoVararg() {
		var log = "This {} is an example.";

		var logPrecessed = service.processLogs(log);

		Assertions.assertEquals(logPrecessed, "This {} is an example.");
	}

}
