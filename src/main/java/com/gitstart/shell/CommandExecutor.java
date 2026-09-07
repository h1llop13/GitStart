package com.gitstart.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {

    public CommandResult execute(Path workingDirectory, String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory.toFile());
        }

        // Объединяем stderr со stdout
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            List<String> output = readLines(process.getInputStream());

            int exitCode = process.waitFor();

            return new CommandResult(
                    exitCode,
                    String.join(System.lineSeparator(), output),
                    ""
            );

        } catch (IOException e) {
            throw new CommandExecutionException(
                    "Failed to execute command: "
                            + String.join(" ", command),
                    e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new CommandExecutionException(
                    "Command execution was interrupted",
                    e
            );
        }
    }

    private List<String> readLines(
            java.io.InputStream inputStream
    ) throws IOException {

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }
}