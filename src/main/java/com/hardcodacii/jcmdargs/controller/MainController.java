package com.hardcodacii.jcmdargs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

/**
 * @author Sandulache Dumitru (sandulachedumitru@hotmail.com)
 */

@Controller
@RequiredArgsConstructor
public class MainController {
	public void start(String[] args) {
		System.out.println("This is my message");
	}
}
