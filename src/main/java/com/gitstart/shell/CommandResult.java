package com.gitstart.shell;

public record CommandResult(
        int exitCode,
        String stdout,
        String stderr
) {

    public boolean isSuccess() {
        return exitCode == 0;
    }
}