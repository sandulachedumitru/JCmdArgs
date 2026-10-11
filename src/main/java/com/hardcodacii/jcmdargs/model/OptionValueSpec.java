package com.hardcodacii.jcmdargs.model;

/**
 * Describes the value policy of an option.
 */
public sealed interface OptionValueSpec
        permits NoOptionValue, EnumOptionValue, FreeOptionValue {
}
