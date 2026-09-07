package com.gitstart.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectDetector {

    public ProjectInfo detect(Path directory) {
        Path normalizedPath = directory.toAbsolutePath().normalize();

        String projectName = normalizedPath.getFileName().toString();
        ProjectType projectType = detectType(normalizedPath);

        return new ProjectInfo(
                projectName,
                normalizedPath,
                projectType
        );
    }

    private ProjectType detectType(Path directory) {
        boolean hasPom = Files.exists(directory.resolve("pom.xml"));
        boolean hasGradle = Files.exists(directory.resolve("build.gradle"))
                || Files.exists(directory.resolve("build.gradle.kts"));
        boolean hasPackageJson = Files.exists(directory.resolve("package.json"));

        if (hasPom) {
            if (isSpringBootMaven(directory.resolve("pom.xml"))) {
                return ProjectType.SPRING_BOOT_MAVEN;
            }

            return ProjectType.JAVA_MAVEN;
        }

        if (hasGradle) {
            if (isSpringBootGradle(directory)) {
                return ProjectType.SPRING_BOOT_GRADLE;
            }

            return ProjectType.JAVA_GRADLE;
        }

        if (hasPackageJson) {
            return ProjectType.NODE_JS;
        }

        return ProjectType.UNKNOWN;
    }

    private boolean isSpringBootMaven(Path pomFile) {
        try {
            String content = Files.readString(pomFile);

            return content.contains("spring-boot")
                    || content.contains("org.springframework.boot");

        } catch (IOException e) {
            return false;
        }
    }

    private boolean isSpringBootGradle(Path directory) {
        Path gradleGroovy = directory.resolve("build.gradle");
        Path gradleKotlin = directory.resolve("build.gradle.kts");

        try {
            if (Files.exists(gradleGroovy)) {
                String content = Files.readString(gradleGroovy);

                if (content.contains("org.springframework.boot")) {
                    return true;
                }
            }

            if (Files.exists(gradleKotlin)) {
                String content = Files.readString(gradleKotlin);

                return content.contains("org.springframework.boot");
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }
}