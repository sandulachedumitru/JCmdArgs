package com.hardcodacii.jcmdargs.model;

public sealed interface ArgumentValueSpec
        permits RequiredArgumentValue, OptionalArgumentValue, DefaultArgumentValue {
}
