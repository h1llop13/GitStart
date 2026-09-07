package com.gitstart.git;

import com.gitstart.shell.CommandExecutor;
import com.gitstart.shell.CommandResult;

import java.nio.file.Files;
import java.nio.file.Path;

public class GitService {

    private final CommandExecutor commandExecutor;

    public GitService(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public boolean isGitRepository(Path directory) {
        return Files.isDirectory(directory.resolve(".git"));
    }

    public boolean hasCommits(Path directory) {
        if (!isGitRepository(directory)) {
            return false;
        }

        CommandResult result = commandExecutor.execute(
                directory,
                "git",
                "rev-parse",
                "HEAD"
        );

        return result.isSuccess();
    }

    public boolean hasRemoteOrigin(Path directory) {
        if (!isGitRepository(directory)) {
            return false;
        }

        CommandResult result = commandExecutor.execute(
                directory,
                "git",
                "remote",
                "get-url",
                "origin"
        );

        return result.isSuccess();
    }

    public GitRepositoryState getState(Path directory) {
        return new GitRepositoryState(
                isGitRepository(directory),
                hasCommits(directory),
                hasRemoteOrigin(directory)
        );
    }

    public CommandResult init(Path directory) {
        return commandExecutor.execute(
                directory,
                "git",
                "init"
        );
    }

    public CommandResult addAll(Path directory) {
        return commandExecutor.execute(
                directory,
                "git",
                "add",
                "."
        );
    }

    public CommandResult commit(Path directory, String message) {
        return commandExecutor.execute(
                directory,
                "git",
                "commit",
                "-m",
                message
        );
    }

    public CommandResult renameBranchToMain(Path directory) {
        return commandExecutor.execute(
                directory,
                "git",
                "branch",
                "-M",
                "main"
        );
    }

    public boolean hasChanges(Path directory) {
        CommandResult result = commandExecutor.execute(
                directory,
                "git",
                "status",
                "--porcelain"
        );

        return result.isSuccess()
                && !result.stdout().isBlank();
    }

    public boolean hasAnyFiles(Path directory) {
        try (var stream = Files.list(directory)) {
            return stream.anyMatch(path ->
                    !path.getFileName().toString().equals(".git")
            );
        } catch (Exception e) {
            return false;
        }
    }
}