package com.hardcodacii.jcmdargs.module.cmd_line_parser.service;

import com.hardcodacii.jcmdargs.module.cmd_line_parser.model.CmdLineArgsActionPerformer;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.Definition;
import com.hardcodacii.jcmdargs.module.definitions_file_parser.model.DefinitionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@FunctionalInterface
public interface CommandLineParserService {
	Optional<CmdLineArgsActionPerformer> parseCmdLine(String pathToDefinitionsFile, Map<DefinitionType, List<Definition>> definitionsMap);
}
