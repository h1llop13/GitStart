package com.gitstart.project;

public class ProjectTypeFormatter {

    public String format(ProjectType type) {
        return switch (type) {
            case JAVA_MAVEN -> "Java / Maven";
            case JAVA_GRADLE -> "Java / Gradle";
            case NODE_JS -> "Node.js";
            case SPRING_BOOT_MAVEN -> "Java / Maven / Spring Boot";
            case SPRING_BOOT_GRADLE -> "Java / Gradle / Spring Boot";
            case UNKNOWN -> "Unknown";
        };
    }
}