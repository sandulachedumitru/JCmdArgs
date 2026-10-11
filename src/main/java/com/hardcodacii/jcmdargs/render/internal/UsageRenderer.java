package com.hardcodacii.jcmdargs.render.internal;

import com.hardcodacii.jcmdargs.model.CliSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class UsageRenderer {

    public String render(CliSpec spec) {
        return render(spec, Optional.empty());
    }

    public String render(CliSpec spec, String programName) {
        Objects.requireNonNull(programName, "programName");

        if (programName.isBlank()) {
            throw new IllegalArgumentException("programName must not be blank");
        }

        return render(spec, Optional.of(programName));
    }

    private String render(CliSpec spec, Optional<String> programName) {
        Objects.requireNonNull(spec, "spec");

        List<String> tokens = new ArrayList<>();

        programName.ifPresent(tokens::add);
        spec.command().ifPresent(command -> tokens.add(command.name()));

        if (!spec.options().isEmpty()) {
            tokens.add("[options]");
        }

        spec.arguments().stream()
                .map(RenderSupport::argumentSignature)
                .forEach(tokens::add);

        if (tokens.isEmpty()) {
            return "Usage:";
        }

        return "Usage: " + String.join(" ", tokens);
    }
}
