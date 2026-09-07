package com.gitstart.project;

import java.nio.file.Path;

public record ProjectInfo(
        String name,
        Path path,
        ProjectType type
) {
}