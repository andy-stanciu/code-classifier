package me.andystanciu.parser.converter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
import me.andystanciu.parser.SolutionType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ASTConverter {
    private String problemName;
    private int solutionNumber;
    private SolutionType type;

    private Path sourcePath;
    private CompilationUnit compilationUnit;

    private int nodeId;
    private final Map<Integer, Node> nodeMappings;

    private ASTConverter() {
        nodeMappings = new HashMap<>();
    }

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

        // export edge list
        var edgeList = new StringBuilder();
        constructEdgeList(compilationUnit, edgeList, null);
        writeToDisk(edgeList.toString(), "edges");

        // export features
        var features = new StringBuilder();
        constructFeatures(features);
        writeToDisk(features.toString(), "features");
    }

    private void constructFeatures(StringBuilder features) {
        Map<Integer, Feature> featureMappings = new HashMap<>();
        var featureVisitor = new FeatureVisitor(featureMappings);

        for (var entry : nodeMappings.entrySet()) {
            int nodeId = entry.getKey();
            var node = entry.getValue();
            node.accept(featureVisitor, nodeId);  // feature visitor does not recurse
        }

        for (var entry : featureMappings.entrySet()) {
            features.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
    }

    private void constructEdgeList(Node node, StringBuilder edgeList, String parentNodeId) {
        String currentNodeId = String.valueOf(nodeId);
        nodeMappings.put(nodeId++, node);

        if (parentNodeId != null && shouldKeep(node)) {
            edgeList.append(parentNodeId).append(" ").append(currentNodeId).append("\n");
        }
        for (Node child : node.getChildNodes().stream().filter(this::shouldKeep).toList()) {
            constructEdgeList(child, edgeList, currentNodeId);
        }
    }

    private boolean shouldKeep(Node node) {
        return !(node instanceof Modifier) &&
                !(node instanceof SimpleName) &&
                !(node instanceof Type);
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
