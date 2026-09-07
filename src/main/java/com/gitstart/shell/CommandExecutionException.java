package com.gitstart.shell;

public class CommandExecutionException extends RuntimeException {

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}