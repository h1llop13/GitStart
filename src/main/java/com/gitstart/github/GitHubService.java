package com.gitstart.github;

import com.gitstart.shell.CommandExecutor;
import com.gitstart.shell.CommandResult;

import java.nio.file.Path;

public class GitHubService {

    private final CommandExecutor commandExecutor;

    public GitHubService(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public CommandResult createRepository(
            Path projectDirectory,
            String repositoryName,
            RepositoryVisibility visibility
    ) {
        String visibilityFlag =
                visibility == RepositoryVisibility.PRIVATE
                        ? "--private"
                        : "--public";

        return commandExecutor.execute(
                projectDirectory,
                "gh",
                "repo",
                "create",
                repositoryName,
                visibilityFlag,
                "--source=.",
                "--remote=origin"
        );
    }

    public CommandResult push(
            Path projectDirectory
    ) {
        return commandExecutor.execute(
                projectDirectory,
                "git",
                "push",
                "-u",
                "origin",
                "main"
        );
    }
}