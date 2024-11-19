package me.andystanciu.parser.converter;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Map;

public final class FeatureVisitor extends VoidVisitorAdapter<Integer> {
    private final Map<Integer, Feature> features;

    public FeatureVisitor(Map<Integer, Feature> features) {
        this.features = features;
    }

    private int getTypeMapping(String type) {
        return type.hashCode();  // type = hashcode of str(type)
    }

    private int getNodeTypeMapping(Node node) {
        return node.getClass().getSimpleName().hashCode();  // nodeType = hashcode of str(node.class)
    }

    private int getValueMapping(Object value) {
        return value.hashCode();  // value = hashed value
    }

    @Override
    public void visit(AnnotationDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(AnnotationMemberDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ArrayAccessExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ArrayCreationExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ArrayInitializerExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValues().size())
        ));
    }

    @Override
    public void visit(AssertStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(AssignExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getOperator().asString())
        ));
    }

    @Override
    public void visit(BinaryExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getOperator().asString())
        ));
    }

    @Override
    public void visit(BlockComment node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.toString())
        ));
    }

    @Override
    public void visit(BlockStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getStatements().size())
        ));
    }

    @Override
    public void visit(BooleanLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(BreakStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(CastExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(CatchClause node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(CharLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(ClassExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                0
        ));
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(CompilationUnit node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ConditionalExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ConstructorDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getDeclarationAsString()),
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ContinueStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(DoStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(DoubleLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(EmptyStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(EnclosedExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(EnumConstantDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getNameAsString()),
                0
        ));
    }

    @Override
    public void visit(EnumDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getNameAsString()),
                0
        ));
    }

    @Override
    public void visit(ExplicitConstructorInvocationStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ExpressionStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(FieldDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ForEachStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ForStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(IfStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(InitializerDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(InstanceOfExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                0
        ));
    }

    @Override
    public void visit(IntegerLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(JavadocComment node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.toString())
        ));
    }

    @Override
    public void visit(LabeledStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(LineComment node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.toString())
        ));
    }

    @Override
    public void visit(LongLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(MarkerAnnotationExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(MemberValuePair node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getNameAsString()),
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(MethodCallExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(MethodDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(NameExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(NormalAnnotationExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(NullLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.toString())
        ));
    }

    @Override
    public void visit(ObjectCreationExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                0
        ));
    }

    @Override
    public void visit(PackageDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(Parameter node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(Name node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getIdentifier())
        ));
    }

    @Override
    public void visit(SimpleName node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getIdentifier())
        ));
    }

    @Override
    public void visit(ArrayCreationLevel node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ReturnStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(SingleMemberAnnotationExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(StringLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(SuperExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(SwitchEntry node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                0
        ));
    }

    @Override
    public void visit(SynchronizedStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ThisExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(TryStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(LocalClassDeclarationStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(UnaryExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getOperator().toString())
        ));
    }

    @Override
    public void visit(VariableDeclarationExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(VariableDeclarator node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(WhileStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(LambdaExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(MethodReferenceExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getIdentifier())
        ));
    }

    @Override
    public void visit(TypeExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                0
        ));
    }

    @Override
    public void visit(NodeList node, Integer nodeId) {
        features.put(nodeId, new Feature(
                0,
                0,
                getValueMapping(node.size())
        ));
    }

    @Override
    public void visit(ImportDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ModuleDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ModuleRequiresDirective node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ModuleExportsDirective node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ModuleProvidesDirective node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ModuleUsesDirective node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(ModuleOpensDirective node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(UnparsableStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(ReceiverParameter node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(SwitchExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(TextBlockLiteralExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getValue())
        ));
    }

    @Override
    public void visit(YieldStmt node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                0
        ));
    }

    @Override
    public void visit(TypePatternExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(RecordDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(CompactConstructorDeclaration node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                0,
                getValueMapping(node.getNameAsString())
        ));
    }

    @Override
    public void visit(RecordPatternExpr node, Integer nodeId) {
        features.put(nodeId, new Feature(
                getNodeTypeMapping(node),
                getTypeMapping(node.getType().toString()),
                0
        ));
    }
}
