package com.hardcodacii.jcmdargs.service.impl;

import com.hardcodacii.jcmdargs.service.RuleService;
import com.hardcodacii.jcmdargs.service.model.Definition;
import com.hardcodacii.jcmdargs.service.model.DefinitionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

	@Override
	public Optional<List<Error>> applyRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		/*
			allowed_arguments_order
				- CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
				- CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
				- CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
			    - CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
				- CHECK IF DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST

			arguments_number
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF allowed_arguments_order DEFINITIONS, MUST BE == 1
				- CHECK THE NUMBER OF ARGUMENTS
				- CHECK IF THE ARGUMENTS HAVE DUPLICATES

			option
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF option DEFINITION, MUST BE > 0
				- CHECK IF THE OPTIONS HAVE DUPLICATES

			command
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF command DEFINITION, MUST BE == 1 ??
				- CHECK IF THE COMMANDS HAVE DUPLICATES ??
		 */
		AllowedArgumentsOrderRules(definitionsMap);
		return Optional.empty();
	}

	/*
		allowed_arguments_order
			- CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
			- CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
			- CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
		    - CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
			- CHECK IF DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST
	 */
	private void AllowedArgumentsOrderRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null) {
			// TODO display error
			return;
		}

		var ALLOWED_ARGUMENTS_ORDER = DefinitionType.ALLOWED_ARGUMENTS_ORDER;

		if (!definitionsMap.containsKey(ALLOWED_ARGUMENTS_ORDER)) {
			// TODO dsplay error
			System.out.println("---- no instance detected for: allowed_arguments_order which must be mandatory and only 1 instance ----");
			return;
		} else if (definitionsMap.get(ALLOWED_ARGUMENTS_ORDER).size() != 1) {
			// TODO dsplay error
			System.out.println("---- duplicate detected for: allowed_arguments_order which must be mandatory and only 1 instance ----");
			return;
		}
	}
}
