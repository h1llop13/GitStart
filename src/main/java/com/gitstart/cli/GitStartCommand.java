package com.gitstart.cli;

import com.gitstart.environment.EnvironmentChecker;
import com.gitstart.git.GitRepositoryState;
import com.gitstart.git.GitService;
import com.gitstart.project.ProjectDetector;
import com.gitstart.project.ProjectInfo;
import com.gitstart.project.ProjectTypeFormatter;
import com.gitstart.shell.CommandExecutor;
import picocli.CommandLine.Command;
import com.gitstart.github.GitHubService;
import com.gitstart.github.RepositoryVisibility;

import java.nio.file.Path;

@Command(
        name = "gitstart",
        mixinStandardHelpOptions = true,
        version = "GitStart 1.0.0",
        description = "Initializes Git repository and connects project to GitHub"
)
public class GitStartCommand implements Runnable {

    @Override
    public void run() {
        System.out.println();
        System.out.println("GitStart");
        System.out.println();

        Path currentDirectory =
                Path.of(System.getProperty("user.dir"));

        ProjectDetector projectDetector =
                new ProjectDetector();

        ProjectTypeFormatter projectTypeFormatter =
                new ProjectTypeFormatter();

        ProjectInfo projectInfo =
                projectDetector.detect(currentDirectory);

        System.out.println(
                "Project: " + projectInfo.name()
        );

        System.out.println(
                "Type: "
                        + projectTypeFormatter.format(
                        projectInfo.type()
                )
        );

        System.out.println();

        CommandExecutor commandExecutor =
                new CommandExecutor();

        EnvironmentChecker environmentChecker =
                new EnvironmentChecker(commandExecutor);

        System.out.println("Checking environment...");

        boolean gitAvailable =
                environmentChecker.isGitAvailable();

        boolean ghAvailable =
                environmentChecker.isGitHubCliAvailable();

        boolean authenticated =
                environmentChecker.isGitHubAuthenticated();

        printStatus(
                "Git installed",
                gitAvailable
        );

        printStatus(
                "GitHub CLI installed",
                ghAvailable
        );

        if (ghAvailable) {
            printStatus(
                    "GitHub authentication detected",
                    authenticated
            );
        }

        if (!gitAvailable) {
            System.err.println();
            System.err.println(
                    "Git is required to use GitStart."
            );
            return;
        }

        if (!ghAvailable) {
            System.err.println();
            System.err.println(
                    "GitHub CLI is required to create GitHub repositories."
            );
            return;
        }

        if (!authenticated) {
            System.err.println();
            System.err.println(
                    "GitHub authentication is required."
            );
            System.err.println(
                    "Run: gh auth login"
            );
            return;
        }

        System.out.println();
        System.out.println(
                "Environment is ready."
        );

        System.out.println();

        GitService gitService =
                new GitService(commandExecutor);

        GitRepositoryState gitState =
                gitService.getState(
                        projectInfo.path()
                );

        System.out.println(
                "Git repository state:"
        );

        printStatus(
                "Local repository detected",
                gitState.repositoryExists()
        );

        if (gitState.repositoryExists()) {
            printStatus(
                    "Existing commit detected",
                    gitState.hasCommits()
            );

            printStatus(
                    "Remote origin detected",
                    gitState.hasRemoteOrigin()
            );
        }

        System.out.println();

        /*
         * Initialize repository if needed
         */
        if (!gitState.repositoryExists()) {

            System.out.println(
                    "Initializing local Git repository..."
            );

            var initResult =
                    gitService.init(
                            projectInfo.path()
                    );

            if (!initResult.isSuccess()) {
                System.err.println(
                        "Failed to initialize Git repository."
                );

                printError(initResult.stderr());
                return;
            }

            System.out.println(
                    "✓ Repository initialized"
            );

        } else {
            System.out.println(
                    "✓ Existing Git repository detected"
            );
        }

        /*
         * Create initial commit if repository
         * does not contain any commits yet
         */
        if (!gitService.hasCommits(
                projectInfo.path()
        )) {

            if (!gitService.hasAnyFiles(
                    projectInfo.path()
            )) {
                System.out.println(
                        "No project files found. Initial commit skipped."
                );
                return;
            }

            System.out.println();
            System.out.println(
                    "Creating initial commit..."
            );

            var addResult =
                    gitService.addAll(
                            projectInfo.path()
                    );

            if (!addResult.isSuccess()) {
                System.err.println(
                        "Failed to stage files."
                );

                printError(addResult.stderr());
                return;
            }

            var commitResult =
                    gitService.commit(
                            projectInfo.path(),
                            "Initial commit"
                    );

            if (!commitResult.isSuccess()) {
                System.err.println(
                        "Failed to create initial commit."
                );

                printError(
                        commitResult.stderr()
                );

                return;
            }

            System.out.println(
                    "✓ Initial commit created"
            );
        }

        /*
         * Configure default branch
         */
        var branchResult =
                gitService.renameBranchToMain(
                        projectInfo.path()
                );

        if (!branchResult.isSuccess()) {
            System.err.println(
                    "Failed to configure main branch."
            );

            printError(
                    branchResult.stderr()
            );

            return;
        }

        System.out.println(
                "✓ Main branch configured"
        );

        GitRepositoryState updatedState =
                gitService.getState(
                        projectInfo.path()
                );

        if (updatedState.hasRemoteOrigin()) {
            System.out.println();
            System.out.println(
                    "✓ Remote origin already configured"
            );
            return;
        }

        ConsolePrompter prompter =
                new ConsolePrompter();

        RepositoryVisibility visibility =
                prompter.askVisibility();

        System.out.println();
        System.out.println(
                "Creating GitHub repository..."
        );

        GitHubService gitHubService =
                new GitHubService(
                        commandExecutor
                );

        var createRepositoryResult =
                gitHubService.createRepository(
                        projectInfo.path(),
                        projectInfo.name(),
                        visibility
                );

        if (!createRepositoryResult.isSuccess()) {
            System.err.println(
                    "Failed to create GitHub repository."
            );

            printError(
                    createRepositoryResult.stdout()
            );

            return;
        }

        System.out.println(
                "✓ GitHub repository created"
        );

        System.out.println(
                "✓ Remote origin configured"
        );

        System.out.println();
        System.out.println(
                "Pushing project to GitHub..."
        );

        var pushResult =
                gitHubService.push(
                        projectInfo.path()
                );

        if (!pushResult.isSuccess()) {
            System.err.println(
                    "Failed to push project."
            );

            printError(
                    pushResult.stdout()
            );

            return;
        }

        System.out.println(
                "✓ Project pushed to GitHub"
        );

        System.out.println();
        System.out.println("Done.");
    }

    private void printStatus(
            String message,
            boolean success
    ) {
        if (success) {
            System.out.println(
                    "✓ " + message
            );
        } else {
            System.out.println(
                    "✗ " + message
            );
        }
    }

    private void printError(
            String error
    ) {
        if (error != null
                && !error.isBlank()) {

            System.err.println(error);
        }
    }
}