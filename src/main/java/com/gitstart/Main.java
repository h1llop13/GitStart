package com.gitstart;

import com.gitstart.cli.GitStartCommand;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GitStartCommand()).execute(args);
        System.exit(exitCode);
    }
}