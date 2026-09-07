package com.gitstart.environment;

import com.gitstart.shell.CommandExecutor;
import com.gitstart.shell.CommandResult;

public class EnvironmentChecker {

    private final CommandExecutor commandExecutor;

    public EnvironmentChecker(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public boolean isGitAvailable() {
        try {
            CommandResult result = commandExecutor.execute(
                    null,
                    "git",
                    "--version"
            );

            return result.isSuccess();

        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isGitHubCliAvailable() {
        try {
            CommandResult result = commandExecutor.execute(
                    null,
                    "gh",
                    "--version"
            );

            return result.isSuccess();

        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isGitHubAuthenticated() {
        try {
            CommandResult result = commandExecutor.execute(
                    null,
                    "gh",
                    "auth",
                    "status"
            );

            return result.isSuccess();

        } catch (RuntimeException e) {
            return false;
        }
    }
}