package me.andystanciu.parser.converter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import me.andystanciu.parser.SolutionType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ASTConverter {
    private String problemName;
    private int solutionNumber;
    private SolutionType type;

    private Path sourcePath;
    private CompilationUnit compilationUnit;

    private int nodeId;
    private final ASTCooccurrenceEncoder cooccurrenceEncoder;

    private ASTConverter(ASTCooccurrenceEncoder cooccurrenceEncoder) {
        this.cooccurrenceEncoder = cooccurrenceEncoder;
    }

    public static ASTConverter withCooccurrenceEncoder(ASTCooccurrenceEncoder cooccurrenceEncoder) {
        return new ASTConverter(cooccurrenceEncoder);
    }

    public ASTConverter withSolution(String problemName, int solutionNumber, SolutionType type) {
        this.problemName = problemName;
        this.solutionNumber = solutionNumber;
        this.type = type;

        sourcePath = Path.of(String.format("%s/%s/%s-%d.txt",
                type.getSolutionPath(), problemName, problemName, solutionNumber));

        try {
            String sourceCode = Files.readString(sourcePath, StandardCharsets.UTF_8);
            var parser = new JavaParser();
            var parseResult = parser.parse(sourceCode);
            parseResult.ifSuccessful(compilationUnit -> {
                this.compilationUnit = compilationUnit;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public void export() {
        if (compilationUnit == null) {
            throw new IllegalStateException("Source program failed to parse");
        }

        // export edge list and update co-occurrence matrix
        var edgeList = new StringBuilder();
        traverseAST(compilationUnit, edgeList, null, null, null,
                cooccurrenceEncoder);
        writeToDisk(edgeList.toString());
    }

    private void traverseAST(Node node, StringBuilder edgeList,
                             String parentNodeId,
                             String parentNodeName,
                             String parentNodeMapping,
                             ASTCooccurrenceEncoder cooccurrenceEncoder) {
        String currentNodeMapping = String.valueOf(cooccurrenceEncoder.getVocabularyEncoding(node.getClass()));
        String currentNodeName = node.getClass().getSimpleName();
        String currentNodeId = String.valueOf(nodeId++);
        cooccurrenceEncoder.updateMapping(node.getClass(), node.getClass());  // update self-occurrence

        if (parentNodeId != null) {
            edgeList.append(parentNodeId).append(" ")
                    .append(parentNodeName).append(" ")
                    .append(parentNodeMapping).append(" ")
                    .append(currentNodeId).append(" ")
                    .append(currentNodeName).append(" ")
                    .append(currentNodeMapping).append("\n");
        }
        for (Node child : node.getChildNodes()) {
            traverseAST(child, edgeList, currentNodeId, currentNodeName, currentNodeMapping, cooccurrenceEncoder);
            cooccurrenceEncoder.updateMapping(child.getClass(), node.getClass());  // update parent-child co-occurrence
        }
    }

    private void writeToDisk(String str) {
        var path = Path.of(String.format("%s/%s", type.getDataPath(), problemName));
        if (!Files.exists(path)) {
            path.toFile().mkdirs();
        }

        String filePath = String.format("%s/%s/%s-%d.edges",
                type.getDataPath(), problemName, problemName, solutionNumber);
        try (var outputStream = new FileOutputStream(filePath)) {
            outputStream.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
