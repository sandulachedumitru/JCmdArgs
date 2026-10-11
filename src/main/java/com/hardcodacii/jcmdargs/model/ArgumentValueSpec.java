package com.hardcodacii.jcmdargs.model;

/**
 * Describes whether a positional argument is required, optional, or defaulted.
 */
public sealed interface ArgumentValueSpec
        permits RequiredArgumentValue, OptionalArgumentValue, DefaultArgumentValue {
}
