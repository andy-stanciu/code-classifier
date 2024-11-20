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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.Map;

public final class ASTCooccurrenceEncoder {
    private final BiMap<Class<?>, Integer> vocabulary;
    private final long[][] cooccurrences;

    private ASTCooccurrenceEncoder(BiMap<Class<?>, Integer> vocabulary) {
        this.vocabulary = vocabulary;
        cooccurrences = new long[vocabulary.size()][vocabulary.size()];
    }

    public int getVocabularyEncoding(Class<?> node) {
        if (!vocabulary.containsKey(node)) {
            throw new IllegalArgumentException(node.getSimpleName() + " is not in the vocabulary");
        }
        return vocabulary.get(node);
    }

    public void updateMapping(Class<?> i, Class<?> j) {
        if (!vocabulary.containsKey(i)) {
            throw new IllegalArgumentException(i.getSimpleName() + " is not in the vocabulary");
        }
        if (!vocabulary.containsKey(j)) {
            throw new IllegalArgumentException(i.getSimpleName() + " is not in the vocabulary");
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

    public static ASTCooccurrenceEncoder withJavaVocabulary() {
        return new ASTCooccurrenceEncoder(javaVocabulary);
    }

    private static final BiMap<Class<?>, Integer> javaVocabulary = HashBiMap.create(new HashMap<>() {{
        put(AnnotationDeclaration.class, 0);
        put(AnnotationMemberDeclaration.class, 1);
        put(ArrayAccessExpr.class, 2);
        put(ArrayCreationExpr.class, 3);
        put(ArrayInitializerExpr.class, 4);
        put(AssertStmt.class, 5);
        put(AssignExpr.class, 6);
        put(BinaryExpr.class, 7);
        put(BlockComment.class, 8);
        put(BlockStmt.class, 9);
        put(BooleanLiteralExpr.class, 10);
        put(BreakStmt.class, 11);
        put(CastExpr.class, 12);
        put(CatchClause.class, 13);
        put(CharLiteralExpr.class, 14);
        put(ClassExpr.class, 15);
        put(ClassOrInterfaceDeclaration.class, 16);
        put(CompilationUnit.class, 17);
        put(ConditionalExpr.class, 18);
        put(ConstructorDeclaration.class, 19);
        put(ContinueStmt.class, 20);
        put(DoStmt.class, 21);
        put(DoubleLiteralExpr.class, 22);
        put(EmptyStmt.class, 23);
        put(EnclosedExpr.class, 24);
        put(EnumConstantDeclaration.class, 25);
        put(EnumDeclaration.class, 26);
        put(ExplicitConstructorInvocationStmt.class, 27);
        put(ExpressionStmt.class, 28);
        put(FieldDeclaration.class, 29);
        put(ForEachStmt.class, 30);
        put(ForStmt.class, 31);
        put(IfStmt.class, 32);
        put(InitializerDeclaration.class, 33);
        put(InstanceOfExpr.class, 34);
        put(IntegerLiteralExpr.class, 35);
        put(JavadocComment.class, 36);
        put(LabeledStmt.class, 37);
        put(LineComment.class, 38);
        put(LongLiteralExpr.class, 39);
        put(MarkerAnnotationExpr.class, 40);
        put(MemberValuePair.class, 41);
        put(MethodCallExpr.class, 42);
        put(MethodDeclaration.class, 43);
        put(NameExpr.class, 44);
        put(NormalAnnotationExpr.class, 45);
        put(NullLiteralExpr.class, 46);
        put(ObjectCreationExpr.class, 47);
        put(PackageDeclaration.class, 48);
        put(Parameter.class, 49);
        put(Name.class, 50);
        put(SimpleName.class, 51);
        put(ArrayCreationLevel.class, 52);
        put(ReturnStmt.class, 53);
        put(SingleMemberAnnotationExpr.class, 54);
        put(StringLiteralExpr.class, 55);
        put(SuperExpr.class, 56);
        put(SwitchEntry.class, 57);
        put(SynchronizedStmt.class, 58);
        put(ThisExpr.class, 59);
        put(TryStmt.class, 60);
        put(LocalClassDeclarationStmt.class, 61);
        put(UnaryExpr.class, 62);
        put(VariableDeclarationExpr.class, 63);
        put(VariableDeclarator.class, 64);
        put(WhileStmt.class, 65);
        put(LambdaExpr.class, 66);
        put(MethodReferenceExpr.class, 67);
        put(TypeExpr.class, 68);
        put(NodeList.class, 69);
        put(ImportDeclaration.class, 70);
        put(ModuleDeclaration.class, 71);
        put(ModuleRequiresDirective.class, 72);
        put(ModuleExportsDirective.class, 73);
        put(ModuleProvidesDirective.class, 74);
        put(ModuleUsesDirective.class, 75);
        put(ModuleOpensDirective.class, 76);
        put(UnparsableStmt.class, 77);
        put(ReceiverParameter.class, 78);
        put(SwitchExpr.class, 79);
        put(TextBlockLiteralExpr.class, 80);
        put(YieldStmt.class, 81);
        put(TypePatternExpr.class, 82);
        put(RecordDeclaration.class, 83);
        put(CompactConstructorDeclaration.class, 84);
        put(RecordPatternExpr.class, 85);
        put(ClassOrInterfaceType.class, 86);
        put(FieldAccessExpr.class, 87);
        put(PrimitiveType.class, 88);
        put(ArrayType.class, 89);
        put(IntersectionType.class, 90);
        put(UnionType.class, 91);
        put(SwitchStmt.class, 92);
        put(ThrowStmt.class, 93);
        put(LocalRecordDeclarationStmt.class, 94);
        put(TypeParameter.class, 95);
        put(UnknownType.class, 96);
        put(VoidType.class, 97);
        put(WildcardType.class, 98);
        put(VarType.class, 99);
        put(Modifier.class, 100);
    }});
}
