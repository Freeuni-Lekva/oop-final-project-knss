package com.example.submission.Utils.Container;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Container {

    private static final String IMAGE = "sandbox-code-executor";

    private static final String CONTAINER_PREFIX = "Sandbox";

    private final String containerName;

    private final Path workDir;

    public Container(Path workDir) {
        containerName = CONTAINER_PREFIX + "-" + UUID.randomUUID();
        this.workDir = workDir;
    }

    public void startContainer() {
        List<String> command = List.of(
                "docker", "run", "-d",
                "--name", containerName,
                "--read-only",
                "--network=none",
                "--memory=256m",
                "--cpus=4.0",
                "--cap-drop=ALL",
                "--cap-add=DAC_READ_SEARCH",
                "--security-opt", "no-new-privileges",
                "-v", workDir + ":/app:rw",
                "-w", "/app",
                IMAGE,
                "sleep", "infinity"
        );

        try {
            Process process = new ProcessBuilder(command).start();
            if (!process.waitFor(5, TimeUnit.SECONDS) || process.exitValue() != 0) {
                throw new RuntimeException("Failed to start sandbox container");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroyContainer() {
        List<String> command = List.of(
                "docker", "rm", "-f", containerName
        );
        try {
            Process process = new ProcessBuilder(command).start();
            if (!process.waitFor(5, TimeUnit.SECONDS) || process.exitValue() != 0) {
                throw new RuntimeException("Failed to destroy sandbox container");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanContainer() {
        List<String> cmd = List.of(
                "docker", "exec", containerName,
                "bash", "-lc",
                "rm -rf /app/* /app/.* 2>/dev/null || true"
        );

        try {
            Process process = new ProcessBuilder(cmd).start();
            if (!process.waitFor(5, TimeUnit.SECONDS) || process.exitValue() != 0) {
                throw new RuntimeException("Failed to clean /app in container " + containerName);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public String getContainerName() {
        return containerName;
    }

    public Path getWorkDir() {
        return workDir;
    }
}
