package com.gitstart.gitignore;

import com.gitstart.project.ProjectType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitignoreService {

    public GitignoreResult prepare(
            Path projectDirectory,
            ProjectType projectType
    ) {
        Path gitignorePath =
                projectDirectory.resolve(".gitignore");

        if (Files.exists(gitignorePath)) {
            return new GitignoreResult(
                    false,
                    true,
                    null
            );
        }

        String templateName =
                resolveTemplate(projectType);

        String resourcePath =
                "/gitignore/" + templateName;

        try (InputStream inputStream =
                     getClass().getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Gitignore template not found: "
                                + resourcePath
                );
            }

            String content =
                    new String(
                            inputStream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            Files.writeString(
                    gitignorePath,
                    content,
                    StandardCharsets.UTF_8
            );

            return new GitignoreResult(
                    true,
                    false,
                    templateName
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to create .gitignore",
                    e
            );
        }
    }

    private String resolveTemplate(
            ProjectType projectType
    ) {
        return switch (projectType) {
            case JAVA_MAVEN,
                 JAVA_GRADLE,
                 SPRING_BOOT_MAVEN,
                 SPRING_BOOT_GRADLE
                    -> "java.gitignore";

            case NODE_JS
                    -> "node.gitignore";

            case UNKNOWN
                    -> "generic.gitignore";
        };
    }
}