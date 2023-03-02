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

	private final static DefinitionType ALLOWED_ARGUMENTS_ORDER = DefinitionType.ALLOWED_ARGUMENTS_ORDER;
	private final static DefinitionType ARGUMENTS_NUMBER = DefinitionType.ARGUMENTS_NUMBER;
	private final static DefinitionType ARGUMENT = DefinitionType.ARGUMENT;
	private final static int ZERO = 0;
	private final static boolean FAILED = false;
	private final static boolean SUCCESSFUL = true;

	@Override
	public Optional<Boolean> applyRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		/*
			allowed_arguments_order
				- CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
				- CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
				- CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
			    - CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
				- CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE
				- CHECK IF ITEM FROM allowed_arguments_order ITEMS LIST HAS DEFINITION INSTANCES (HAS IMPLEMENTATION) IN DEF FILE, AND VICE VERSA

			arguments_number
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF arguments_number DEFINITIONS, MUST BE == 1

			argument
				- CHECK IF THE NUMBER OF ARGUMENTS MATCH VALUE OF arguments_number AND IF THE VALUE IS AN INTEGER NUMBER
				- CHECK IF THE ARGUMENTS HAVE DUPLICATES

			option
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF option DEFINITION, MUST BE > 0
				- CHECK IF THE OPTIONS HAVE DUPLICATES

			command
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF command DEFINITION, MUST BE == 1 ??
				- CHECK IF THE COMMANDS HAVE DUPLICATES ??
		 */
		if (allowedArgumentsOrderRules(definitionsMap)) displayService.infoLn("Applying [{}] rules: SUCCESSFUL", ALLOWED_ARGUMENTS_ORDER.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: FAILED", ALLOWED_ARGUMENTS_ORDER.getArgumentCode());

		if (argumentNumberRules(definitionsMap)) displayService.infoLn("Applying [{}] rules: SUCCESSFUL", ARGUMENTS_NUMBER.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: FAILED", ARGUMENTS_NUMBER.getArgumentCode());

		if (argumentRules(definitionsMap)) displayService.infoLn("Applying [{}] rules: SUCCESSFUL", ARGUMENT.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: SUCCESSFUL", ARGUMENT.getArgumentCode());

		return errorService.getErrors().size() == ZERO ? Optional.of(true) : Optional.of(false);
	}

	/*
		allowed_arguments_order
			- CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
			- CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
			- CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
		    - CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
			- CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE
			- CHECK IF ITEM FROM allowed_arguments_order ITEMS LIST HAS DEFINITION INSTANCES (HAS IMPLEMENTATION) IN DEF FILE, AND VICE VERSA
	 */
	private boolean allowedArgumentsOrderRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null) {
			errorService.addError(new Error(displayService.errorLn("Cannot apply rules for [{}] because map of definitions is null", ALLOWED_ARGUMENTS_ORDER.getArgumentCode())));
			return FAILED;
		}

		// CHECK IF allowed_arguments_order DEFINITION EXISTS. IS mandatory
		if (!definitionsMap.containsKey(ALLOWED_ARGUMENTS_ORDER)) {
			errorService.addError(new Error(displayService.errorLn("No instance detected for [{}] which must be mandatory and only 1 instance per definitions file", ALLOWED_ARGUMENTS_ORDER.getArgumentCode())));
			return FAILED;
		}

		// CHECK IF allowed_arguments_order DEFINITION HAVE DUPLICATES
		if (definitionsMap.get(ALLOWED_ARGUMENTS_ORDER).size() != 1) {
			errorService.addError(new Error(displayService.errorLn("Duplicate detected for [{}] which must be mandatory and only 1 instance per definitions file", ALLOWED_ARGUMENTS_ORDER.getArgumentCode())));
			return FAILED;
		}

		// CHECK IF DEFINITIONS FROM allowed_arguments_order LIST HAVE INSTANCES. The list size != 0
		var listOfInstances = definitionsMap.get(ALLOWED_ARGUMENTS_ORDER);
		var definition = listOfInstances.get(ZERO);
		if (!(definition instanceof DefinitionNonOption)) {
			errorService.addError(new Error(displayService.errorLn("[{}] must be of [DefinitionNonOption] type, but found [{}]", ALLOWED_ARGUMENTS_ORDER.getArgumentCode(), definition.getClass().getSimpleName())));
			return FAILED;
		}
		var listOfPossibleValues = ((DefinitionNonOption) definition).getPossibleValues();
		if (listOfPossibleValues.size() == ZERO) {
			errorService.addError(new Error(displayService.errorLn("[{}] definition type list doesn't contains any definition type", ALLOWED_ARGUMENTS_ORDER.getArgumentCode())));
			return FAILED;
		}

		// CHECK IF LIST ITEMS FROM allowed_arguments_order HAVE DUPLICATES
		var duplicates = rule_HaveDuplicates(listOfPossibleValues);
		if (duplicates.size() != ZERO) {
			errorService.addError(new Error(displayService.errorLn("[{}] definition type list contains duplicates: {}", ALLOWED_ARGUMENTS_ORDER.getArgumentCode(), duplicates)));
		}
		// CHECK IF ONLY DEFINITION TYPES ARE CONTAINED IN  allowed_arguments_order ITEMS LIST, AND NOT UNDEFINED TYPE
		var unknownDefType = rule_ContainsOnlyDefinedTypes(listOfPossibleValues);
		if (unknownDefType.size() != ZERO) {
			errorService.addError(new Error(displayService.errorLn("[{}] definition type list contains unknown or not allowed definition type: {}", ALLOWED_ARGUMENTS_ORDER.getArgumentCode(), unknownDefType)));
		}
		// CHECK IF ITEM FROM allowed_arguments_order ITEMS LIST HAS DEFINITION INSTANCES (HAS IMPLEMENTATION) IN DEF FILE, AND VICE VERSA
		Set<String> unimplementedInstances;
		unimplementedInstances = rule_DefinitionInstancesAreSpecifiedIn_allowed_arguments_order_itemList(definitionsMap, listOfPossibleValues);
		if (unimplementedInstances.size() != ZERO) {
			errorService.addError(new Error(displayService.errorLn("Definitions file contains definitions that are not specified in [{}] item list: {}", ALLOWED_ARGUMENTS_ORDER.getArgumentCode(), unimplementedInstances)));
		}
		unimplementedInstances = rule_allowed_arguments_order_itemListHasDefinitionInstances(definitionsMap, listOfPossibleValues);
		if (unimplementedInstances.size() != ZERO) {
			errorService.addError(new Error(displayService.errorLn("Some items from [{}] item list definitions have not definition instances: {}", ALLOWED_ARGUMENTS_ORDER.getArgumentCode(), unimplementedInstances)));
		}

		// RETURN
		return errorService.getErrors().size() == ZERO ? SUCCESSFUL : FAILED;
	}

	// return list of errors if found any
	private static Set<String> rule_ContainsOnlyDefinedTypes(List<String> listOfPossibleValues) {
		Set<String> unknownType = new HashSet<>();
		for (var val : listOfPossibleValues) {
			var defType = DefinitionType.getDefinitionTypeByCode(val);
			if (defType == null || defType.equals(DefinitionType.ALLOWED_ARGUMENTS_ORDER)) unknownType.add(val);
		}


		return unknownType;
	}

	// return list of errors if found any
	private static <T> Set<T> rule_HaveDuplicates(List<T> listOfPossibleValues) {
		Set<T> duplicates = new HashSet<>();
		Set<T> unique = new HashSet<>();

		for (T t : listOfPossibleValues)
			if (!unique.add(t)) duplicates.add(t);

		return duplicates;
	}

	// return list of errors if found any
	private static Set<String> rule_DefinitionInstancesAreSpecifiedIn_allowed_arguments_order_itemList(Map<DefinitionType, List<Definition>> definitionsMap, List<String> listOfPossibleValues) {
		Set<String> unimplementedInstances = new HashSet<>();
		for (var entry : definitionsMap.entrySet()) {
			var argType = entry.getKey().getArgumentCode();
			if (!listOfPossibleValues.contains(argType) && !argType.equals(ALLOWED_ARGUMENTS_ORDER.getArgumentCode()))
				unimplementedInstances.add(argType);
		}

		return unimplementedInstances;
	}

	// return list of errors if found any
	private static Set<String> rule_allowed_arguments_order_itemListHasDefinitionInstances(Map<DefinitionType, List<Definition>> definitionsMap, List<String> listOfPossibleValues) {
		Set<String> unimplementedInstances = new HashSet<>();
		for (var val : listOfPossibleValues) {
			var defType = DefinitionType.getDefinitionTypeByCode(val);
			if (defType == null || !definitionsMap.containsKey(defType))
				unimplementedInstances.add(val);
		}

		return unimplementedInstances;
	}

	/*
	arguments_number
		- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF arguments_number DEFINITIONS, MUST BE == 1
		- CHECK IF THE NUMBER OF ARGUMENTS MATCH VALUE OF arguments_number AND IF THE VALUE IS AN INTEGER NUMBER
    */
	private boolean argumentNumberRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null) {
			errorService.addError(new Error(displayService.errorLn("Cannot apply rules for [{}] definition because map of definitions is null", ARGUMENTS_NUMBER.getArgumentCode())));
			return FAILED;
		}

		var numberOfArgumentsCountedInDefFile = definitionsMap.get(ARGUMENT).size();

		// IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF arguments_number DEFINITIONS, MUST BE == 1
		if (definitionsMap.containsKey(ARGUMENTS_NUMBER)) {
			// CHECK IF arguments_number DEFINITION HAVE DUPLICATES
			if (definitionsMap.get(ARGUMENTS_NUMBER).size() != 1) {
				errorService.addError(new Error(displayService.errorLn("Duplicate detected for [{}] definition which must be only 1 instance per definitions file", ARGUMENTS_NUMBER.getArgumentCode())));
				return FAILED;
			}
		} else {
			displayService.warningLn("No instance detected for [{}] definition. If want to defined it than there must be only 1 instance per definitions file", ARGUMENTS_NUMBER.getArgumentCode());
			if (numberOfArgumentsCountedInDefFile != ZERO) {
				var log = "[{}] will be set to [{}] which is the detected number of [{}] instances found in definitions file" +
						", but for more control, it is recommended that " +
						"in definition file, specify the allowed number of arguments (ex: [{}=N], where N is a positive integer)";
				displayService.infoLn(log, ARGUMENTS_NUMBER.getArgumentCode(), numberOfArgumentsCountedInDefFile, ARGUMENT.getArgumentCode(), ARGUMENTS_NUMBER.getArgumentCode());

				// create a bew DefinitionNonOption
				DefinitionNonOption def = new DefinitionNonOption();
				def.setType(ARGUMENTS_NUMBER);
				def.getPossibleValues().add(String.valueOf(numberOfArgumentsCountedInDefFile));

				// add definition to list
				List<Definition> defList = new ArrayList<>();
				defList.add(def);

				// add arguments_number to definitionMap
				definitionsMap.put(ARGUMENTS_NUMBER, defList);
			}
		}

		return SUCCESSFUL;
	}

	/*
		argument
			- CHECK IF THE NUMBER OF ARGUMENTS MATCH VALUE OF arguments_number AND IF THE VALUE IS AN INTEGER NUMBER
			- CHECK IF THE ARGUMENTS HAVE DUPLICATES
	 */
	private boolean argumentRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null) {
			errorService.addError(new Error(displayService.errorLn("Cannot apply rules for [{}] definition because map of definitions is null", ARGUMENTS_NUMBER.getArgumentCode())));
			return FAILED;
		}

		var numberOfArgumentsCountedInDefFile = definitionsMap.get(ARGUMENT).size();

		// CHECK IF THE NUMBER OF ARGUMENTS MATCH VALUE OF arguments_number AND IF THE VALUE IS A POSITIVE INTEGER NUMBER
		var definition = definitionsMap.get(ARGUMENTS_NUMBER).get(ZERO);
		if (! (definition instanceof DefinitionNonOption)) {
			errorService.addError(new Error(displayService.errorLn("[{}] must be of [DefinitionNonOption] type, but found [{}]", ARGUMENTS_NUMBER.getArgumentCode(), definition.getClass().getSimpleName())));
			return FAILED;
		}
		int argumentNumberSpecifiedValue;
		var value = ((DefinitionNonOption) definition).getPossibleValues().get(ZERO);
		try {
			argumentNumberSpecifiedValue = Integer.parseInt(value);
			if (argumentNumberSpecifiedValue < 0) throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			errorService.addError(new Error(displayService.errorLn("The value for [{}] must be a positive integer number. Found [{}]", ARGUMENTS_NUMBER.getArgumentCode(), value)));
			return FAILED;
		}
		if (argumentNumberSpecifiedValue != numberOfArgumentsCountedInDefFile) {
			errorService.addError(new Error(displayService.errorLn("[{}={}], but found [{}] argument definitions", ARGUMENTS_NUMBER.getArgumentCode(), argumentNumberSpecifiedValue, numberOfArgumentsCountedInDefFile)));
			return FAILED;
		}

		// CHECK IF THE ARGUMENTS HAVE DUPLICATES
		// TODO

		return SUCCESSFUL;
	}
}
