package me.andystanciu.parser.demo;

import me.andystanciu.parser.converter.ASTConverter;
import me.andystanciu.parser.converter.ASTCooccurrenceEncoder;
import me.andystanciu.parser.preprocessing.SolutionParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Demo {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("An unexpected error occurred.");
            return;
        }

        String sourceCode = args[0];
        boolean parses = SolutionParser.builder()
                .withString(sourceCode)
                .validate();

        if (!parses) {
            System.out.println("Solution does not compile yet...");
            return;
        }

        String edges = ASTConverter.withCooccurrenceEncoder(ASTCooccurrenceEncoder.withJavaVocabulary())
                .withString(sourceCode)
                .getEdges();

        invokeModel(edges);
    }

    private static void invokeModel(String edges) {
        try {
            // Command to execute the Python script with an argument
            String[] command = {"python3", "../../src/model/demo.py", edges};

            // Create a ProcessBuilder instance
            ProcessBuilder pb = new ProcessBuilder(command);

            // Redirect error stream to the standard output
            pb.redirectErrorStream(true);

            // Start the process
            Process process = pb.start();

            // Read the output of the Python script
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete and get the exit code
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
