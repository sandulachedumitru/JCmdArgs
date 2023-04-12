package com.hardcodacii.jcmdargs.module.cmd_line_parser.service.impl;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.model.CmdLineArgsActionPerformer;
import com.hardcodacii.jcmdargs.module.cmd_line_parser.service.CommandLineParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class CommandLineParserServiceImpl implements CommandLineParserService {
	@Override
	public Optional<CmdLineArgsActionPerformer> parseCmdLine(String definitionsFile) {
		return Optional.empty();
	}
}
