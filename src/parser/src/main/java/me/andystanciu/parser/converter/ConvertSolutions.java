package me.andystanciu.parser.converter;

import me.andystanciu.parser.SolutionType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public final class ConvertSolutions {

    public static void main(String[] args) {
        var rawCooccurrenceEncoder = ASTCooccurrenceEncoder.withJavaVocabulary();
        var redactedCooccurrenceEncoder = ASTCooccurrenceEncoder.withJavaVocabulary();
        var redactedStrippedCooccurrenceEncoder = ASTCooccurrenceEncoder.withJavaVocabulary();

        convertSolutions(SolutionType.RAW, rawCooccurrenceEncoder);
        convertSolutions(SolutionType.REDACTED, redactedCooccurrenceEncoder);
        convertSolutions(SolutionType.REDACTED_STRIPPED, redactedStrippedCooccurrenceEncoder);
    }

    private static void convertSolutions(SolutionType type, ASTCooccurrenceEncoder cooccurrenceEncoder) {
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
                ASTConverter.withCooccurrenceEncoder(cooccurrenceEncoder)
                        .withSolution(problemName, i, type)
                        .export();
            }
            System.out.printf("Converted %d solutions for %s%n",
                    solutionCount, problemName);
        }

        // export co-occurrence matrix
        var cooccurrences = cooccurrenceEncoder.vectorize();
        var sb = new StringBuilder();
        for (Map.Entry<Integer, long[]> entry : cooccurrences.entrySet()) {
            sb.append(entry.getKey()).append(':');
            for (long l : entry.getValue()) {
                sb.append(' ').append(l);
            }
            sb.append('\n');
        }

        String filePath = String.format("%s.cooccurrences", type.getDataPath());
        try (var outputStream = new FileOutputStream(filePath)) {
            outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
