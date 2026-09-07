package com.gitstart.cli;

import com.gitstart.github.RepositoryVisibility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsolePrompter {

    private final BufferedReader reader =
            new BufferedReader(
                    new InputStreamReader(System.in)
            );

    public RepositoryVisibility askVisibility() {
        System.out.println();
        System.out.println("Repository visibility:");
        System.out.println("1. Private");
        System.out.println("2. Public");
        System.out.print("Choose [1]: ");

        try {
            String input = reader.readLine();

            if (input == null || input.isBlank()) {
                return RepositoryVisibility.PRIVATE;
            }

            if (input.trim().equals("2")) {
                return RepositoryVisibility.PUBLIC;
            }

            return RepositoryVisibility.PRIVATE;

        } catch (IOException e) {
            return RepositoryVisibility.PRIVATE;
        }
    }
}