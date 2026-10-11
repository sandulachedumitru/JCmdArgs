package com.hardcodacii.jcmdargs.jca;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public record JcaSource(
        String name,
        String content
) {
    public JcaSource {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(content, "content");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    public static JcaSource of(String name, String content) {
        return new JcaSource(name, content);
    }

    public static JcaSource from(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return new JcaSource(path.toString(), Files.readString(path));
    }
}
