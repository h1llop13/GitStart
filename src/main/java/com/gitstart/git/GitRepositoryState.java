package com.gitstart.git;

public record GitRepositoryState(
        boolean repositoryExists,
        boolean hasCommits,
        boolean hasRemoteOrigin
) {
}