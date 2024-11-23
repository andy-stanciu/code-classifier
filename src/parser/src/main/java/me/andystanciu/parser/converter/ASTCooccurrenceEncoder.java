package me.andystanciu.parser.converter;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;

import java.util.HashMap;
import java.util.Map;

public final class ASTCooccurrenceEncoder {
    private final Map<String, Integer> vocabulary;
    private final long[][] cooccurrences;

    private ASTCooccurrenceEncoder(Map<String, Integer> vocabulary) {
        this.vocabulary = vocabulary;
        cooccurrences = new long[vocabulary.size()][vocabulary.size()];
    }

    public int getVocabularyEncoding(Node node) {
        String name = getNodeName(node);
        if (!vocabulary.containsKey(name)) {
            throw new IllegalArgumentException(name + " is not in the vocabulary");
        }
        return vocabulary.get(name);
    }

    public void updateMapping(Node node1, Node node2) {
        String i = getNodeName(node1);
        String j = getNodeName(node2);

        if (!vocabulary.containsKey(i)) {
            throw new IllegalArgumentException(i + " is not in the vocabulary");
        }
        if (!vocabulary.containsKey(j)) {
            throw new IllegalArgumentException(j + " is not in the vocabulary");
        }

        int n = vocabulary.get(i);
        int m = vocabulary.get(j);

        cooccurrences[n][m]++;
        cooccurrences[m][n]++;
    }

    public Map<Integer, long[]> vectorize() {
        Map<Integer, long[]> vectorized = new HashMap<>();
        for (int i = 0; i < cooccurrences.length; i++) {
            vectorized.put(i, cooccurrences[i]);
        }
        return vectorized;
    }

    public String getNodeName(Node node) {
        String name = node.getClass().getSimpleName();
        if (node instanceof BinaryExpr b) {
            name += toPascalCase(b.getOperator().toString());
        } else if (node instanceof UnaryExpr u) {
            name += toPascalCase(u.getOperator().toString());
        } else if (node instanceof AssignExpr a) {
            name += toPascalCase(a.getOperator().toString());
        }
        return name;
    }

    private String toPascalCase(String str) {
        var result = new StringBuilder();
        String[] words = str.split("_");
        for (String word : words) {
            result.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    public static ASTCooccurrenceEncoder withJavaVocabulary() {
        return new ASTCooccurrenceEncoder(javaVocabulary);
    }

    private static final Map<String, Integer> javaVocabulary = new HashMap<>() {{
        put(AnnotationDeclaration.class.getSimpleName(), 0);
        put(AnnotationMemberDeclaration.class.getSimpleName(), 1);
        put(ArrayAccessExpr.class.getSimpleName(), 2);
        put(ArrayCreationExpr.class.getSimpleName(), 3);
        put(ArrayInitializerExpr.class.getSimpleName(), 4);
        put(AssertStmt.class.getSimpleName(), 5);
        put(AssignExpr.class.getSimpleName(), 6);
        put("BinaryExprOr", 7);
        put(BlockComment.class.getSimpleName(), 8);
        put(BlockStmt.class.getSimpleName(), 9);
        put(BooleanLiteralExpr.class.getSimpleName(), 10);
        put(BreakStmt.class.getSimpleName(), 11);
        put(CastExpr.class.getSimpleName(), 12);
        put(CatchClause.class.getSimpleName(), 13);
        put(CharLiteralExpr.class.getSimpleName(), 14);
        put(ClassExpr.class.getSimpleName(), 15);
        put(ClassOrInterfaceDeclaration.class.getSimpleName(), 16);
        put(CompilationUnit.class.getSimpleName(), 17);
        put(ConditionalExpr.class.getSimpleName(), 18);
        put(ConstructorDeclaration.class.getSimpleName(), 19);
        put(ContinueStmt.class.getSimpleName(), 20);
        put(DoStmt.class.getSimpleName(), 21);
        put(DoubleLiteralExpr.class.getSimpleName(), 22);
        put(EmptyStmt.class.getSimpleName(), 23);
        put(EnclosedExpr.class.getSimpleName(), 24);
        put(EnumConstantDeclaration.class.getSimpleName(), 25);
        put(EnumDeclaration.class.getSimpleName(), 26);
        put(ExplicitConstructorInvocationStmt.class.getSimpleName(), 27);
        put(ExpressionStmt.class.getSimpleName(), 28);
        put(FieldDeclaration.class.getSimpleName(), 29);
        put(ForEachStmt.class.getSimpleName(), 30);
        put(ForStmt.class.getSimpleName(), 31);
        put(IfStmt.class.getSimpleName(), 32);
        put(InitializerDeclaration.class.getSimpleName(), 33);
        put(InstanceOfExpr.class.getSimpleName(), 34);
        put(IntegerLiteralExpr.class.getSimpleName(), 35);
        put(JavadocComment.class.getSimpleName(), 36);
        put(LabeledStmt.class.getSimpleName(), 37);
        put(LineComment.class.getSimpleName(), 38);
        put(LongLiteralExpr.class.getSimpleName(), 39);
        put(MarkerAnnotationExpr.class.getSimpleName(), 40);
        put(MemberValuePair.class.getSimpleName(), 41);
        put(MethodCallExpr.class.getSimpleName(), 42);
        put(MethodDeclaration.class.getSimpleName(), 43);
        put(NameExpr.class.getSimpleName(), 44);
        put(NormalAnnotationExpr.class.getSimpleName(), 45);
        put(NullLiteralExpr.class.getSimpleName(), 46);
        put(ObjectCreationExpr.class.getSimpleName(), 47);
        put(PackageDeclaration.class.getSimpleName(), 48);
        put(Parameter.class.getSimpleName(), 49);
        put(Name.class.getSimpleName(), 50);
        put(SimpleName.class.getSimpleName(), 51);
        put(ArrayCreationLevel.class.getSimpleName(), 52);
        put(ReturnStmt.class.getSimpleName(), 53);
        put(SingleMemberAnnotationExpr.class.getSimpleName(), 54);
        put(StringLiteralExpr.class.getSimpleName(), 55);
        put(SuperExpr.class.getSimpleName(), 56);
        put(SwitchEntry.class.getSimpleName(), 57);
        put(SynchronizedStmt.class.getSimpleName(), 58);
        put(ThisExpr.class.getSimpleName(), 59);
        put(TryStmt.class.getSimpleName(), 60);
        put(LocalClassDeclarationStmt.class.getSimpleName(), 61);
        put(UnaryExpr.class.getSimpleName(), 62);
        put(VariableDeclarationExpr.class.getSimpleName(), 63);
        put(VariableDeclarator.class.getSimpleName(), 64);
        put(WhileStmt.class.getSimpleName(), 65);
        put(LambdaExpr.class.getSimpleName(), 66);
        put(MethodReferenceExpr.class.getSimpleName(), 67);
        put(TypeExpr.class.getSimpleName(), 68);
        put(NodeList.class.getSimpleName(), 69);
        put(ImportDeclaration.class.getSimpleName(), 70);
        put(ModuleDeclaration.class.getSimpleName(), 71);
        put(ModuleRequiresDirective.class.getSimpleName(), 72);
        put(ModuleExportsDirective.class.getSimpleName(), 73);
        put(ModuleProvidesDirective.class.getSimpleName(), 74);
        put(ModuleUsesDirective.class.getSimpleName(), 75);
        put(ModuleOpensDirective.class.getSimpleName(), 76);
        put(UnparsableStmt.class.getSimpleName(), 77);
        put(ReceiverParameter.class.getSimpleName(), 78);
        put(SwitchExpr.class.getSimpleName(), 79);
        put(TextBlockLiteralExpr.class.getSimpleName(), 80);
        put(YieldStmt.class.getSimpleName(), 81);
        put(TypePatternExpr.class.getSimpleName(), 82);
        put(RecordDeclaration.class.getSimpleName(), 83);
        put(CompactConstructorDeclaration.class.getSimpleName(), 84);
        put(RecordPatternExpr.class.getSimpleName(), 85);
        put(ClassOrInterfaceType.class.getSimpleName(), 86);
        put(FieldAccessExpr.class.getSimpleName(), 87);
        put(PrimitiveType.class.getSimpleName(), 88);
        put(ArrayType.class.getSimpleName(), 89);
        put(IntersectionType.class.getSimpleName(), 90);
        put(UnionType.class.getSimpleName(), 91);
        put(SwitchStmt.class.getSimpleName(), 92);
        put(ThrowStmt.class.getSimpleName(), 93);
        put(LocalRecordDeclarationStmt.class.getSimpleName(), 94);
        put(TypeParameter.class.getSimpleName(), 95);
        put(UnknownType.class.getSimpleName(), 96);
        put(VoidType.class.getSimpleName(), 97);
        put(WildcardType.class.getSimpleName(), 98);
        put(VarType.class.getSimpleName(), 99);
        put(Modifier.class.getSimpleName(), 100);
        put("BinaryExprAnd", 101);
        put("BinaryExprBinaryOr", 102);
        put("BinaryExprBinaryAnd", 103);
        put("BinaryExprXor", 104);
        put("BinaryExprEquals", 105);
        put("BinaryExprNotEquals", 106);
        put("BinaryExprLess", 107);
        put("BinaryExprGreater", 108);
        put("BinaryExprLessEquals", 109);
        put("BinaryExprGreaterEquals", 110);
        put("BinaryExprLeftShift", 111);
        put("BinaryExprSignedRightShift", 112);
        put("BinaryExprUnsignedRightShift", 113);
        put("BinaryExprPlus", 114);
        put("BinaryExprMinus", 115);
        put("BinaryExprMultiply", 116);
        put("BinaryExprDivide", 117);
        put("BinaryExprRemainder", 118);
        put("AssignExprAssign", 119);
        put("AssignExprPlus", 119);
        put("AssignExprMinus", 120);
        put("AssignExprMultiply", 121);
        put("AssignExprDivide", 122);
        put("AssignExprBinaryAnd", 123);
        put("AssignExprBinaryOr", 124);
        put("AssignExprXor", 125);
        put("AssignExprRemainder", 126);
        put("AssignExprLeftShift", 127);
        put("AssignExprSignedRightShift", 128);
        put("AssignExprUnsignedRightShift", 129);
        put("UnaryExprPlus", 130);
        put("UnaryExprMinus", 131);
        put("UnaryExprPrefixIncrement", 132);
        put("UnaryExprPrefixDecrement", 133);
        put("UnaryExprLogicalComplement", 134);
        put("UnaryExprBitwiseComplement", 135);
        put("UnaryExprPostfixIncrement", 136);
        put("UnaryExprPostfixDecrement", 137);
    }};
}
