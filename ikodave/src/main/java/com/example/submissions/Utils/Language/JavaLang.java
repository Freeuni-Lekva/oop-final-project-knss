package com.example.submissions.Utils.Language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.example.submissions.CodeRunner.DockerCodeRunner.SANDBOX_USER;

public class JavaLang implements CodeLang {

    private static final String JAVA_FILE_NAME = "Solution";

    @Override
    public void createFiles(Path path, String solutionCode) {
        try {
            Files.writeString(path.resolve(JAVA_FILE_NAME + ".java"), solutionCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> compileCommand(String containerName) {
        return List.of(
                "docker", "exec", containerName,
                "javac", JAVA_FILE_NAME + ".java"
        );
    }

    @Override
    public List<String> executeCommand(String containerName) {
        return List.of(
                "docker", "exec", "-i", containerName,
                "java", "-cp", "/app", JAVA_FILE_NAME
        );
    }
}