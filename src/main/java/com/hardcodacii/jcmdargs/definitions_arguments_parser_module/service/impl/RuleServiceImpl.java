package com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.impl;

import com.hardcodacii.jcmdargs.commons_module.global.SystemEnvironmentVariable;
import com.hardcodacii.jcmdargs.commons_module.service.DisplayService;
import com.hardcodacii.jcmdargs.commons_module.service.ErrorService;
import com.hardcodacii.jcmdargs.commons_module.service.model.Error;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.RuleService;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model.Definition;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model.DefinitionNonOption;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model.DefinitionOption;
import com.hardcodacii.jcmdargs.definitions_arguments_parser_module.service.model.DefinitionType;
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
	private final static DefinitionType ALLOWED_ARGUMENTS_ORDER = DefinitionType.ALLOWED_ARGUMENTS_ORDER;
	private final static DefinitionType ARGUMENTS_NUMBER = DefinitionType.ARGUMENTS_NUMBER;
	private final static DefinitionType ARGUMENT = DefinitionType.ARGUMENT;
	private final static DefinitionType OPTION = DefinitionType.OPTION;
	private final static DefinitionType COMMAND = DefinitionType.COMMAND;
	private final static int ZERO = 0;
	private final static int ONE = 1;
	private final static int TWO = 2;
	private final static boolean FAILED = false;
	private final static boolean SUCCESSFUL = true;
	private final static String EMPTY_STRING = "";
	private final DisplayService displayService;
	private final ErrorService errorService;
	private final SystemEnvironmentVariable environment;

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
	private static <T> Set<T> rule_HaveDuplicates(List<T> itemList) {
		Set<T> duplicates = new HashSet<>();
		Set<T> unique = new HashSet<>();

		for (T t : itemList)
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

	private static void createArgumentNumberWithValue(Map<DefinitionType, List<Definition>> definitionsMap, int value) {
		// create a bew DefinitionNonOption
		DefinitionNonOption def = new DefinitionNonOption();
		def.setType(ARGUMENTS_NUMBER);
		def.getPossibleValues().add(String.valueOf(value));

		// add definition to list
		List<Definition> defList = new ArrayList<>();
		defList.add(def);

		// add arguments_number to definitionMap
		definitionsMap.put(ARGUMENTS_NUMBER, defList);
	}

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
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF option DEFINITION, MUST BE > 0 ----> THIS RULE IS DETECTED IN allowed_arguments_order RULES
				- CHECK IF NUMBER OF OPTION DEFINITIONS ( ex: {--debug,-d} ) >= 1 AND <= 2
				- CHECK IF OPTIONS DEFINITION, IF MORE THAN 1, HAVE 2 FORM: LONG OR SHORT (ex: {--help, -h})
				- CHECK IF THE OPTIONS HAVE DUPLICATES

			command
				- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF command DEFINITION, MUST BE == 1 ??  ----> THIS RULE IS DETECTED IN allowed_arguments_order RULES
				- CHECK IF THE COMMAND HAVE MORE THAN 1 INSTANCE
		 */
		if (allowedArgumentsOrderRules(definitionsMap))
			displayService.infoLn("Applying [{}] rules: SUCCESSFUL", ALLOWED_ARGUMENTS_ORDER.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: FAILED", ALLOWED_ARGUMENTS_ORDER.getArgumentCode());

		if (argumentNumberRules(definitionsMap))
			displayService.infoLn("Applying [{}] rules: SUCCESSFUL", ARGUMENTS_NUMBER.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: FAILED", ARGUMENTS_NUMBER.getArgumentCode());

		if (argumentRules(definitionsMap))
			displayService.infoLn("Applying [{}] rules: SUCCESSFUL", ARGUMENT.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: FAILED", ARGUMENT.getArgumentCode());

		if (optionRules(definitionsMap))
			displayService.infoLn("Applying [{}] rules: SUCCESSFUL", OPTION.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: FAILED", OPTION.getArgumentCode());

		if (commandRules(definitionsMap))
			displayService.infoLn("Applying [{}] rules: SUCCESSFUL", COMMAND.getArgumentCode());
		else displayService.infoLn("Applying [{}] rules: FAILED", COMMAND.getArgumentCode());

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

		// IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF arguments_number DEFINITIONS, MUST BE == 1
		if (definitionsMap.containsKey(ARGUMENTS_NUMBER)) {
			// CHECK IF arguments_number DEFINITION HAVE DUPLICATES
			var argNumDefList = definitionsMap.get(ARGUMENTS_NUMBER);
			if (argNumDefList.size() != 1) {
				errorService.addError(new Error(displayService.errorLn("Duplicate detected for [{}] definition which must be only 1 instance per definitions file", ARGUMENTS_NUMBER.getArgumentCode())));
				return FAILED;
			}
			var argNumDef = argNumDefList.get(ZERO);
			if (!(argNumDef instanceof DefinitionNonOption)) {
				errorService.addError(new Error(displayService.errorLn("[{}] must be of [DefinitionNonOption] type, but found [{}]", ARGUMENTS_NUMBER.getArgumentCode(), argNumDef.getClass().getSimpleName())));
				return FAILED;
			}
			var possibleValuesListSize = ((DefinitionNonOption) argNumDefList.get(ZERO)).getPossibleValues();
			if (possibleValuesListSize.size() != 1) {
				errorService.addError(new Error(displayService.errorLn("[{}] definition can have one single value. Found: {}", ARGUMENTS_NUMBER.getArgumentCode(), possibleValuesListSize)));
				return FAILED;
			}
		} else {
			displayService.warningLn("No instance detected for [{}] definition. If want to defined it than there must be only 1 instance per definitions file", ARGUMENTS_NUMBER.getArgumentCode());

			var argsDef = definitionsMap.get(ARGUMENT);
			var numberOfArgumentsCountedInDefFile = argsDef == null ? ZERO : argsDef.size();

			var log = "[{}] will be set to [{}] which is the detected number of [{}] instances found in definitions file" +
					", but for more control, it is recommended that " +
					"in definition file, specify the allowed number of arguments (ex: [{}=N], where N is a positive integer)";
			displayService.infoLn(log, ARGUMENTS_NUMBER.getArgumentCode(), numberOfArgumentsCountedInDefFile, ARGUMENT.getArgumentCode(), ARGUMENTS_NUMBER.getArgumentCode());
			createArgumentNumberWithValue(definitionsMap, numberOfArgumentsCountedInDefFile);
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
			errorService.addError(new Error(displayService.errorLn("Cannot apply rules for [{}] defNum because map of definitions is null", ARGUMENT.getArgumentCode())));
			return FAILED;
		}

		if (!definitionsMap.containsKey(ARGUMENT)) {
			displayService.warning("No instances detected for [{}]", ARGUMENT.getArgumentCode());
			return SUCCESSFUL;
		}

		// CHECK IF THE NUMBER OF ARGUMENTS MATCH VALUE OF arguments_number AND IF THE VALUE IS A POSITIVE INTEGER NUMBER
		var defNum = definitionsMap.get(ARGUMENTS_NUMBER).get(ZERO);
		int argumentNumberSpecifiedValue;
		var value = ((DefinitionNonOption) defNum).getPossibleValues().get(ZERO);
		try {
			argumentNumberSpecifiedValue = Integer.parseInt(value);
			if (argumentNumberSpecifiedValue < 0) throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			errorService.addError(new Error(displayService.errorLn("The value for [{}] must be a positive integer number. Found [{}]", ARGUMENTS_NUMBER.getArgumentCode(), value)));
			return FAILED;
		}
		var argsDef = definitionsMap.get(ARGUMENT);
		var numberOfArgumentsCountedInDefFile = argsDef == null ? ZERO : argsDef.size();
		if (argumentNumberSpecifiedValue != numberOfArgumentsCountedInDefFile) {
			errorService.addError(new Error(displayService.errorLn("[{}={}], but found [{}] argument definitions", ARGUMENTS_NUMBER.getArgumentCode(), argumentNumberSpecifiedValue, numberOfArgumentsCountedInDefFile)));
			return FAILED;
		}

		// CHECK IF THE ARGUMENTS HAVE DUPLICATES
		var duplicated = rule_HaveDuplicates(argsDef);
		if (!duplicated.isEmpty()) {
			errorService.addError(new Error(displayService.errorLn("Duplicate detected for [{}]: {}", ARGUMENT.getArgumentCode(),
					duplicated.stream().map(d -> (DefinitionNonOption) d).map(DefinitionNonOption::getPossibleValues).collect(Collectors.toSet())
			)));
			return FAILED;
		}

		return SUCCESSFUL;
	}

	/*
		option
			- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF option DEFINITION, MUST BE > 0 ----> THIS RULE IS DETECTED IN allowed_arguments_order RULES
			- CHECK IF NUMBER OF OPTION DEFINITIONS ( ex: {--debug,-d} ) >= 1 AND <= 2
			- CHECK IF OPTIONS DEFINITION, IF MORE THAN 1, HAVE 2 FORM: LONG OR SHORT (ex: {--help, -h})
			- CHECK IF THE OPTIONS HAVE DUPLICATES
	 */
	private boolean optionRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null) {
			errorService.addError(new Error(displayService.errorLn("Cannot apply rules for [{}] defNum because map of definitions is null", OPTION.getArgumentCode())));
			return FAILED;
		}

		if (!definitionsMap.containsKey(OPTION)) {
			displayService.warning("No instances detected for [{}]", OPTION.getArgumentCode());
			return SUCCESSFUL;
		}

		var optsDefList = definitionsMap.get(OPTION);

		Map<String, Integer> duplicatedOptsMap = new HashMap<>();
		var haveError = false;
		for (var def : optsDefList) {
			// CHECK IF NUMBER OF OPTION DEFINITIONS ( ex: {--debug,-d} ) >= 1 AND <= 2
			if (!(def instanceof DefinitionOption)) {
				errorService.addError(new Error(displayService.errorLn("[{}] must be of [DefinitionNonOption] type, but found [{}]", OPTION.getArgumentCode(), def.getClass().getSimpleName())));
				return FAILED;
			}
			var allowedValuesList = ((DefinitionOption) def).getAllowedValues();
			var optsDefinitionsList = ((DefinitionOption) def).getOptsDefinitions();

			var failedRule1 = allowedValuesList == null || optsDefinitionsList == null;
			var failedRule2 = allowedValuesList.size() >= ONE && allowedValuesList.get(ZERO).equals(EMPTY_STRING);
			var failedRule3 = optsDefinitionsList.size() >= ONE && optsDefinitionsList.get(ZERO).equals(EMPTY_STRING);
			var failedRule4 = !(ONE <= allowedValuesList.size());
			var failedRule5 = !(ONE <= optsDefinitionsList.size() && optsDefinitionsList.size() <= TWO);
			var agregatedFailedRules = failedRule1 || failedRule3 || failedRule5;

			if (agregatedFailedRules) {
				errorService.addError(new Error(displayService.errorLn("The number of options definitions (ex: {--debug,-d}) for [{}] must be equal with 1 or 2. Found: {}", OPTION.getArgumentCode(), optsDefinitionsList)));
				haveError = true;
			}

			// CHECK IF OPTIONS DEFINITION, IF MORE THAN 1, HAVE 2 FORM: LONG OR SHORT (ex: {--help, -h})
			var LONG = environment.TOKEN_SPECIAL_CHAR_OPTION_PREFIX_LONG;
			var SHORT = environment.TOKEN_SPECIAL_CHAR_OPTION_PREFIX_SHORT.toString();
			var foundLongAndShort = false;

			// format, must start with -- or -
			if (optsDefinitionsList.size() == TWO) {
				var optDef1 = optsDefinitionsList.get(ZERO);
				var optDef2 = optsDefinitionsList.get(ONE);

				if ((optDef1.startsWith(LONG) && optDef2.startsWith(SHORT)) || (optDef1.startsWith(SHORT) && optDef2.startsWith(LONG))) {
					foundLongAndShort = true;
				} else {
					errorService.addError(new Error(displayService.errorLn("The options must be prefixed with [{}] and [{}] (ex: {--help, -h}). Found: [{},{}]", LONG, SHORT, optDef1, optDef2)));
					haveError = true;
				}
			} else {
				// optsDefinitionsList.size() == 1
				var optDef = optsDefinitionsList.get(ZERO);
				if (optDef.startsWith(LONG) || optDef.startsWith(SHORT)) {
					foundLongAndShort = true;
				} else {
					errorService.addError(new Error(displayService.errorLn("The options must be prefixed with [{}] or [{}] (ex: {--help} or {-h}). Found: [{}]", LONG, SHORT, optDef)));
					haveError = true;
				}
			}

			// CHECK IF THE OPTIONS HAVE DUPLICATES
			for (var key : optsDefinitionsList) {
				if (duplicatedOptsMap.containsKey(key)) {
					var value = duplicatedOptsMap.get(key) + 1;
					duplicatedOptsMap.put(key, value);
					haveError = true;
				} else duplicatedOptsMap.put(key, 1);
			}
		}

		// display duplicate if any
		for (var entry : duplicatedOptsMap.entrySet())
			if (entry.getValue() > 1)
				errorService.addError(new Error(displayService.errorLn("Duplicate detected in [{}] definition. Found: [{}] instances of [{}]", OPTION.getArgumentCode(), entry.getValue(), entry.getKey())));

		return haveError ? FAILED : SUCCESSFUL;
	}

	/*
		command
			- IF SPECIFIED IN allowed_arguments_order CHECK THE NUMBER OF command DEFINITION, MUST BE == 1 ??  ----> THIS RULE IS DETECTED IN allowed_arguments_order RULES
			- CHECK IF THE COMMAND HAVE MORE THAN 1 INSTANCE
	 */
	private boolean commandRules(Map<DefinitionType, List<Definition>> definitionsMap) {
		if (definitionsMap == null) {
			errorService.addError(new Error(displayService.errorLn("Cannot apply rules for [{}] defNum because map of definitions is null", COMMAND.getArgumentCode())));
			return FAILED;
		}

		if (!definitionsMap.containsKey(COMMAND)) {
			displayService.warning("No instances detected for [{}]", COMMAND.getArgumentCode());
			return SUCCESSFUL;
		}

		// CHECK IF THE COMMAND HAVE MORE THAN 1 INSTANCE
		var cmdDefinitionList = definitionsMap.get(COMMAND);
		if (cmdDefinitionList.size() != ONE) {
			errorService.addError(new Error(displayService.errorLn("[{}] have more than 1 definition per definition file. Found [{}]: [{}]", COMMAND.getArgumentCode(), cmdDefinitionList.size(), cmdDefinitionList)));
			return FAILED;
		}

		return SUCCESSFUL;
	}
}
