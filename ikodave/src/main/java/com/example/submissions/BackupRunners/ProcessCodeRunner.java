package com.example.submissions.BackupRunners;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProcessCodeRunner {
    private static final String userCode = """
            import java.util.*;
            public class Solution {
                public static int solve(List<Integer> nums) {                   
                    int sum = 0;
                    for (int x : nums) sum += x;
                    return sum;
                }
            }
            """;

    private static final String testRunnerCode = """
                import java.util.*;
                public class TestRunner {
                    public static void main(String[] args) {
                        List<Integer> input = Arrays.stream(args[0].split(","))
                                                   .map(Integer::parseInt)
                                                   .toList();
                    }
                }
            """;

    private static final Path WORKDIR = Paths.get(
            System.getProperty("java.io.tmpdir"), "submission-runner");

    private static final long TIMEOUT_SEC = 3;

    private void runProcess() throws IOException, InterruptedException {

        Files.createDirectories(WORKDIR);
        Files.writeString(WORKDIR.resolve("Solution.java"), userCode);
        Files.writeString(WORKDIR.resolve("TestRunner.java"), testRunnerCode);


        CodeValidator.validateCode(userCode);

        Process compile = new ProcessBuilder("javac", "Solution.java", "TestRunner.java")
                .directory(WORKDIR.toFile())
                .redirectErrorStream(true)
                .start();

        if (!compile.waitFor(10, TimeUnit.SECONDS) || compile.exitValue() != 0) {
            System.err.println(new String(compile.getInputStream().readAllBytes()));
            return;
        }


        String testInput = "2,3,5";
        String expected  = "10";

        List<String> runCommands = Arrays.asList(
                "java",
                "-cp",
                WORKDIR.toString(),
                "TestRunner",
                testInput
        );

        Process run = new ProcessBuilder(runCommands)
                .directory(WORKDIR.toFile())
                .redirectErrorStream(true)
                .start();

        boolean result = run.waitFor(TIMEOUT_SEC, TimeUnit.SECONDS);


        if (!result) {
            return;
        }

        InputStream inputStream= run.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder outputBuilder = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            outputBuilder.append(line);
        }
        String output = outputBuilder.toString().trim();

        FileUtils.deleteDirectory(WORKDIR.toFile());
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessCodeRunner processCodeRunner = new ProcessCodeRunner();
        processCodeRunner.runProcess();
    }


}
