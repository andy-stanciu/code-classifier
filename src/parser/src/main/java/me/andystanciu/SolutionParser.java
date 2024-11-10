package me.andystanciu;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class SolutionParser {
    private static final String SOLUTIONS_DIR = "./solutions";
    private static final String REDACTED_SOLUTIONS_DIR = "./solutions/redacted";
    private static final String REDACTED_STRIPPED_SOLUTIONS_DIR = "./solutions/redacted-stripped";

    private final Map<String, String> identifierMappings;
    private final Map<String, Integer> occurrences;
    private final Set<String> keywords;

    private String problemName;
    private int solutionNumber;

    private CompilationUnit compilationUnit;

    private SolutionParser() {
        identifierMappings = new HashMap<>();
        occurrences = new HashMap<>();
        keywords = new HashSet<>();
    }

    public static SolutionParser builder() {
        return new SolutionParser();
    }

    public SolutionParser withSolution(String problemName, int solutionNumber) {
        this.problemName = problemName;
        this.solutionNumber = solutionNumber;

        var path = Path.of(String.format("%s/raw/%s/%s-%d.txt",
                SOLUTIONS_DIR, problemName, problemName, solutionNumber));

        for (var keyword : problemName.split("-")) {
            if (keyword.length() > 1) {
                keywords.add(keyword.toLowerCase());
            }
        }

        try {
            String sourceCode = Files.readString(path, StandardCharsets.UTF_8);
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

    public SolutionParser redactSensitiveIdentifiers() {
        if (compilationUnit == null) {
            System.out.printf("Skipping further processing of %s-%s.txt, reason: failed to parse%n",
                    problemName, solutionNumber);
            return this;
        }

        var symbolSolver = new JavaSymbolSolver(new CombinedTypeSolver());
        StaticJavaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        compilationUnit.accept(new ObfuscateDeclarationVisitor(), null);
        compilationUnit.accept(new ObfuscateUsageVisitor(), null);
        writeToDisk(REDACTED_SOLUTIONS_DIR);

        return this;
    }

    public SolutionParser stripComments() {
        if (compilationUnit == null) {
            System.out.printf("Skipping further processing of %s-%s.txt, reason: failed to parse%n",
                    problemName, solutionNumber);
            return this;
        }

        compilationUnit.getAllContainedComments().forEach(Comment::remove);
        writeToDisk(REDACTED_STRIPPED_SOLUTIONS_DIR);
        return this;
    }

    public boolean complete() {
        return compilationUnit != null;
    }

    private void writeToDisk(String dir) {
        var path = Path.of(String.format("%s/%s", dir, problemName));
        if (!Files.exists(path)) {
            path.toFile().mkdirs();
        }

        String filePath = String.format("%s/%s/%s-%d.txt",
                dir, problemName, problemName, solutionNumber);
        try (var outputStream = new FileOutputStream(filePath)) {
            outputStream.write(compilationUnit.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.printf("Saved solution: %s%n", filePath);
    }

    private final class ObfuscateDeclarationVisitor extends ModifierVisitor<Void> {
        @Override
        public Visitable visit(ClassOrInterfaceDeclaration cid, Void arg) {
            var mapping = createIdentifierMapping(cid.getNameAsString(), cid);
            mapping.ifPresent(cid::setName);
            return super.visit(cid, arg);
        }

        @Override
        public Visitable visit(MethodDeclaration md, Void arg) {
            var mapping = createIdentifierMapping(md.getNameAsString(), md);
            mapping.ifPresent(md::setName);
            return super.visit(md, arg);
        }

        @Override
        public Visitable visit(Parameter param, Void arg) {
            var mapping = createIdentifierMapping(param.getNameAsString(), param);
            mapping.ifPresent(param::setName);
            return super.visit(param, arg);
        }

        @Override
        public Visitable visit(VariableDeclarator vd, Void arg) {
            var mapping = createIdentifierMapping(vd.getNameAsString(), vd);
            mapping.ifPresent(vd::setName);
            return super.visit(vd, arg);
        }

        @Override
        public Visitable visit(FieldDeclaration fd, Void arg) {
            fd.getVariables().forEach(f -> {
                var mapping = createIdentifierMapping(f.getNameAsString(), f);
                mapping.ifPresent(f::setName);
            });
            return super.visit(fd, arg);
        }
    }

    private final class ObfuscateUsageVisitor extends ModifierVisitor<Void> {
        @Override
        public Visitable visit(SimpleName sn, Void arg) {
            var mapping = getIdentifierMapping(sn.getIdentifier());
            mapping.ifPresent(sn::setIdentifier);
            return super.visit(sn, arg);
        }
    }

    private Optional<String> getIdentifierMapping(String id) {
        if (identifierMappings.containsKey(id)) {
            return Optional.of(identifierMappings.get(id));
        }
        return Optional.empty();
    }

    private Optional<String> createIdentifierMapping(String id, Node type) {
        if (identifierMappings.containsKey(id)) {
            return Optional.of(identifierMappings.get(id));
        }

        // Only create a mapping if the identifier matches any of the keywords
        if (!shouldRedact(id)) {
            return Optional.empty();
        }

        String identifierName = getIdentifierName(type);
        int idNumber = occurrences.getOrDefault(identifierName, 0) + 1;
        occurrences.put(identifierName, idNumber);

        String mapping = identifierName + idNumber;
        identifierMappings.put(id, mapping);

        return Optional.of(mapping);
    }

    private boolean shouldRedact(String id) {
        id = id.toLowerCase();
        for (var keyword : keywords) {
            if (id.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String getIdentifierName(Node type) {
        if (type instanceof ClassOrInterfaceDeclaration) {
            return "Class";
        } else if (type instanceof MethodDeclaration) {
            return "method";
        } else if (type instanceof VariableDeclarator) {
            return "var";
        } else if (type instanceof Parameter) {
            return "param";
        }
        return "id";
    }
}
