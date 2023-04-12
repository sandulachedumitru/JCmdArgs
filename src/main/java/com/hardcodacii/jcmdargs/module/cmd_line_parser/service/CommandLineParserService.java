package com.hardcodacii.jcmdargs.module.cmd_line_parser.service;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.model.CmdLineArgsActionPerformer;

import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

public interface CommandLineParserService {
	Optional<CmdLineArgsActionPerformer> parseCmdLine(String definitionsFile);
}
