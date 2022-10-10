package com.hardcodacii.jcmdargs.service.imp;

import com.hardcodacii.jcmdargs.service.model.Argument;
import com.hardcodacii.jcmdargs.service.CmdLineDefinitionParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class CmdLineDefinitionParserServiceImpl implements CmdLineDefinitionParserService {
	@Override
	public Map<String, Argument> parseArguments() {



		return null;
	}
}
