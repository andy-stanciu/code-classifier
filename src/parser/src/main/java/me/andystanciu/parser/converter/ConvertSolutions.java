package me.andystanciu.parser.converter;

import me.andystanciu.parser.SolutionType;

import java.nio.file.Path;
import java.util.Objects;

public final class ConvertSolutions {
    private static final String SOLUTIONS_DIR = "./solutions";

    public static void main(String[] args) {
        convertSolutions(SolutionType.RAW);
        convertSolutions(SolutionType.REDACTED);
        convertSolutions(SolutionType.REDACTED_STRIPPED);
    }

    private static void convertSolutions(SolutionType type) {
        var path = Path.of(type.getSolutionPath());
        var directory = path.toFile();
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.printf("%s does not exist!", path);
            return;
        }

        for (var solutionDir : Objects.requireNonNull(directory.listFiles())) {
            if (!solutionDir.isDirectory()) {
                continue;
            }
            String problemName = solutionDir.getName();
            int solutionCount = Objects.requireNonNull(solutionDir.listFiles()).length;
            for (int i = 1; i <= solutionCount; i++) {
                ASTConverter.builder()
                        .withSolution(problemName, i, type)
                        .export();
            }
            System.out.printf("Converted %d solutions to edges and features for %s%n",
                    solutionCount, problemName);
        }
    }
}
