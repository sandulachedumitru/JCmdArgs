package com.hardcodacii.jcmdargs.model;

public sealed interface OptionValueSpec
        permits NoOptionValue, EnumOptionValue, FreeOptionValue {
}
