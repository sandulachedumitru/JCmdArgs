package com.hardcodacii.jcmdargs.service.impl;

import com.hardcodacii.jcmdargs.service.DisplayService;
import com.hardcodacii.jcmdargs.service.ErrorService;
import com.hardcodacii.jcmdargs.service.RuleService;
import com.hardcodacii.jcmdargs.service.model.Definition;
import com.hardcodacii.jcmdargs.service.model.DefinitionNonOption;
import com.hardcodacii.jcmdargs.service.model.DefinitionType;
import com.hardcodacii.jcmdargs.service.model.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Dumitru Săndulache (sandulachedumitru@hotmail.com)
 */

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {
	private final DisplayService displayService;
	private final ErrorService errorService;

	@Override
	public Optional<Boolean> applyRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		/*
			allowed_arguments_order
				- CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
				- CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
				- CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
			    - CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
				- CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE

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
		if (AllowedArgumentsOrderRules(definitionsMap)) {
			// TODO print status: OK, FAILED, etc
			return Optional.of(true);
		} else return Optional.of(false);
	}

	/*
		allowed_arguments_order
			- CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
			- CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
			- CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
		    - CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
			- CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE
	 */
	private boolean AllowedArgumentsOrderRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		var FAILED = false;
		var SUCCESSFUL = true;

		if (definitionsMap == null) {
			errorService.addError(new Error(displayService.errorLn("Cannot apply rules for [{}] because map of definitions is null", "allowed_arguments_order")));
			return FAILED;
		}

		var ALLOWED_ARGUMENTS_ORDER = DefinitionType.ALLOWED_ARGUMENTS_ORDER;
		int ZERO = 0;

		// CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
		if (!definitionsMap.containsKey(ALLOWED_ARGUMENTS_ORDER)) {
			errorService.addError(new Error(displayService.errorLn("No instance detected for: [{}] which must be mandatory and only 1 instance per definitions file", "allowed_arguments_order")));
			return FAILED;
		}

		// CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
		if (definitionsMap.get(ALLOWED_ARGUMENTS_ORDER).size() != 1) {
			errorService.addError(new Error(displayService.errorLn("Duplicate detected for: : [{}] which must be mandatory and only 1 instance per definitions file", "allowed_arguments_order")));
			return FAILED;
		}

		// CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
		var listOfInstances = definitionsMap.get(ALLOWED_ARGUMENTS_ORDER);
		var definition = listOfInstances.get(ZERO);
		if (!(definition instanceof DefinitionNonOption)) {
			errorService.addError(new Error(displayService.errorLn("[{}] must be of [DefinitionNonOption] type, but found [{}]", "allowed_arguments_order", definition.getClass().getSimpleName())));
			return FAILED;
		}
		var listOfPossibleValues = ((DefinitionNonOption) definition).getPossibleValues();
		if (listOfPossibleValues.size() == ZERO) {
			errorService.addError(new Error(displayService.errorLn("[{}] definition type list doesn't contains any definition type", "allowed_arguments_order")));
			return FAILED;
		}
		
		// CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
		var duplicates = haveDuplicates(listOfPossibleValues);
		if (duplicates.size() != ZERO) {
			errorService.addError(new Error(displayService.errorLn("[{}] definition type list contains duplicates: [{}]", "allowed_arguments_order", duplicates)));
		}
		// CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE
		var unknownDefType = containsOnlyTypesDefined(listOfPossibleValues);
		if (unknownDefType.size() != ZERO) {
			errorService.addError(new Error(displayService.errorLn("[{}] definition type list contains unknown or not allowed definition type: [{}]", "allowed_arguments_order", unknownDefType)));
		}

		return errorService.getErrors().size() != 0 ? FAILED : SUCCESSFUL;
	}

	// return list of errors if found any
	private Set<String> containsOnlyTypesDefined(List<String> listOfPossibleValues) {
		Set<String> unknownType = new HashSet<>();
		for (String val : listOfPossibleValues) {
			var defType = DefinitionType.getDefinitionTypeByCode(val);
			if (defType == null || defType.equals(DefinitionType.ALLOWED_ARGUMENTS_ORDER))
				unknownType.add(val);
		}


		return unknownType;
	}

	// return list of errors if found any
	private <T> Set<T> haveDuplicates(List<T> listOfPossibleValues) {
		Set<T> duplicates = new HashSet<>();
		Set<T> unique = new HashSet<>();

		for (T t : listOfPossibleValues)
			if (!unique.add(t))
				duplicates.add(t);

		return duplicates;
	}
}
