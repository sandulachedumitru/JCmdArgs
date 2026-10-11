package com.hardcodacii.jcmdargs.jca.internal;

import com.hardcodacii.jcmdargs.diagnostic.SourceLocation;

public sealed interface JcaStatement
        permits CommandDeclaration,
                OptionDeclaration,
                ArgumentDeclaration,
                ArgumentsNumberDeclaration,
                OrderDeclaration,
                DocumentationDeclaration {

    SourceLocation sourceLocation();
}
