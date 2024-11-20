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

    public void exportDot() {
        if (compilationUnit == null) {
            throw new IllegalStateException("Source program failed to parse");
        }

        // export dot visual representation
        var dot = new StringBuilder("digraph AST {\n");
        traverseDot(compilationUnit, dot, null);
        dot.append("}");
        writeToDisk(dot.toString(), "dot");
    }

    private void traverseDot(Node node, StringBuilder dot, String parentNodeId) {
        String currentNodeId = "node" + (nodeId++);
        dot.append(String.format("%s [label=\"%s\"];\n", currentNodeId, node.getClass().getSimpleName()));

        if (parentNodeId != null) {
            dot.append(String.format("%s -> %s;\n", parentNodeId, currentNodeId));
        }

        for (Node child : node.getChildNodes()) {
            traverseDot(child, dot, currentNodeId);
        }
    }

    public void exportEdges() {
        if (compilationUnit == null) {
            throw new IllegalStateException("Source program failed to parse");
        }

        // export edge list and update co-occurrence matrix
        var edgeList = new StringBuilder();
        traverseEdges(compilationUnit, edgeList, null, null, null,
                cooccurrenceEncoder);
        writeToDisk(edgeList.toString(), "edges");
    }

    private void traverseEdges(Node node, StringBuilder edgeList,
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
            traverseEdges(child, edgeList, currentNodeId, currentNodeName, currentNodeMapping, cooccurrenceEncoder);
            cooccurrenceEncoder.updateMapping(child.getClass(), node.getClass());  // update parent-child co-occurrence
        }
    }

    private void writeToDisk(String str, String extension) {
        var path = Path.of(String.format("%s/%s", type.getDataPath(), problemName));
        if (!Files.exists(path)) {
            path.toFile().mkdirs();
        }

        String filePath = String.format("%s/%s/%s-%d.%s",
                type.getDataPath(), problemName, problemName, solutionNumber, extension);
        try (var outputStream = new FileOutputStream(filePath)) {
            outputStream.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
