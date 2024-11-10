package me.andystanciu;

import java.nio.file.Path;
import java.util.Objects;

public class Main {
    private static final String SOLUTIONS_DIR = "./solutions";

    public static void main(String[] args) {
        var path = Path.of(String.format("%s/raw", SOLUTIONS_DIR));

        var rawSolutionsDirectory = path.toFile();
        if (!rawSolutionsDirectory.exists() || !rawSolutionsDirectory.isDirectory()) {
            System.out.printf("%s does not exist! Have you scraped any problems yet?%n", path);
            return;
        }

        for (var solutionDir : Objects.requireNonNull(rawSolutionsDirectory.listFiles())) {
            String problemName = solutionDir.getName();
            int solutionCount = Objects.requireNonNull(solutionDir.listFiles()).length;
            System.out.printf("Processing %d solutions for %s...%n",
                    solutionCount, problemName);
            int processedCount = 0;
            for (int i = 1; i <= solutionCount; i++) {
                boolean success = SolutionParser.builder()
                        .withSolution(problemName, i)
                        .redactSensitiveIdentifiers()
                        .stripComments()
                        .complete();
                if (success) {
                    processedCount++;
                }
            }
            System.out.printf("Successfully processed %d/%d solutions for %s%n",
                    processedCount, solutionCount, problemName);
        }
    }
}