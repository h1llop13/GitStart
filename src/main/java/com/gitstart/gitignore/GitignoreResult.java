package com.gitstart.gitignore;

public record GitignoreResult(
        boolean created,
        boolean alreadyExists,
        String templateName
) {
}