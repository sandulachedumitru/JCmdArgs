package com.hardcodacii.jcmdargs.service.impl;

import com.hardcodacii.jcmdargs.service.RuleService;
import com.hardcodacii.jcmdargs.service.model.Definition;
import com.hardcodacii.jcmdargs.service.model.DefinitionNonOption;
import com.hardcodacii.jcmdargs.service.model.DefinitionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
		AllowedArgumentsOrderRules(definitionsMap);
		return Optional.empty();
	}

	/*
		allowed_arguments_order
			- CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
			- CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
			- CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
		    - CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
			- CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE
	 */
	private void AllowedArgumentsOrderRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null) {
			// TODO display error
			return;
		}

		var ALLOWED_ARGUMENTS_ORDER = DefinitionType.ALLOWED_ARGUMENTS_ORDER;
		int ZERO = 0;

		// CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
		if (!definitionsMap.containsKey(ALLOWED_ARGUMENTS_ORDER)) {
			// TODO dsplay error
			System.out.println("---- no instance detected for: allowed_arguments_order which must be mandatory and only 1 instance ----");
			return;
		}

		// CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
		if (definitionsMap.get(ALLOWED_ARGUMENTS_ORDER).size() != 1) {
			// TODO dsplay error
			System.out.println("---- duplicate detected for: allowed_arguments_order which must be mandatory and only 1 instance ----");
			return;
		}

		// CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
		var listOfInstances = definitionsMap.get(ALLOWED_ARGUMENTS_ORDER);
		var definition = listOfInstances.get(ZERO);
		if (!(definition instanceof DefinitionNonOption)) {
			// TODO dsplay error
			System.out.println("---- allowed_arguments_order must be of DefinitionNonOption type, but found {} ----");
			return;
		}
		var listOfPossibleValues = ((DefinitionNonOption) definition).getPossibleValues();
		if (listOfPossibleValues.size() == ZERO) {
			// TODO dsplay error
			System.out.println("---- allowed_arguments_order definition type list doesn't contains any definition type ----");
			return;
		}
		
		// CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
		var duplicates = haveDuplicates(listOfPossibleValues);
		if (duplicates.size() != ZERO) {
			// TODO dsplay error
			System.out.println("---- allowed_arguments_order item list contains duplicates ----");
			System.out.println(duplicates);
		}
		// CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE
		var unknownDefType = containsOnlyTypesDefined(listOfPossibleValues);
		if (unknownDefType.size() != ZERO) {
			// TODO dsplay error
			System.out.println("---- allowed_arguments_order item list contains unknown or unallowed definition type ----");
			System.out.println(unknownDefType);
		}
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
