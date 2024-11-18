package me.andystanciu;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

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

    private ASTConverter() {}

    public static ASTConverter builder() { return new ASTConverter(); }

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

        StringBuilder edgeList = new StringBuilder();
        traverseNode(compilationUnit, edgeList, null);
        writeToDisk(edgeList.toString());
    }

    private void traverseNode(Node node, StringBuilder edgeList, String parentNodeId) {
        String currentNodeId = String.valueOf(nodeId++);
        if (parentNodeId != null) {
            edgeList.append(parentNodeId).append(" ").append(currentNodeId).append("\n");
        }

        for (Node child : node.getChildNodes()) {
            traverseNode(child, edgeList, currentNodeId);
        }
    }

    private void writeToDisk(String str) {
        var path = Path.of(String.format("%s/%s", type.getDataPath(), problemName));
        if (!Files.exists(path)) {
            path.toFile().mkdirs();
        }

        String filePath = String.format("%s/%s/%s-%d.txt",
                type.getDataPath(), problemName, problemName, solutionNumber);
        try (var outputStream = new FileOutputStream(filePath)) {
            outputStream.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
