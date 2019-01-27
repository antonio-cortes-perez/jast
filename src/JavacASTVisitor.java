/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class JavacASTVisitor extends TreePathScanner<Void, JavacASTNode> {

  private final CompilationUnitTree compilationUnitTree;
  private JavacASTNode root;
  private final Trees trees;
  private final SourcePositions sourcePositions;

  static JavacASTNode visit(CompilationUnitTree tree, Trees trees) {
    JavacASTVisitor visitor = new JavacASTVisitor(tree, trees);
    visitor.scan(tree, null);
    return visitor.root;
  }

  private JavacASTVisitor(CompilationUnitTree compilationUnitTree, Trees trees) {
    this.compilationUnitTree = compilationUnitTree;
    this.trees = trees;
    sourcePositions = trees.getSourcePositions();
  }

  private JavacASTNode process(Tree tree, JavacASTNode parent) {
    Element element = trees.getElement(getCurrentPath());
    TypeMirror typeMirror = trees.getTypeMirror(getCurrentPath());
    long startPosition = sourcePositions.getStartPosition(compilationUnitTree, tree);
    long endPosition = sourcePositions.getEndPosition(compilationUnitTree, tree);
    JavacASTNode node = new JavacASTNode(tree, element, typeMirror, startPosition, endPosition);
    parent.addChild(node);
    return node;
  }

  @Override
  public Void visitCompilationUnit(CompilationUnitTree tree, JavacASTNode unused) {
    Element element = trees.getElement(getCurrentPath());
    TypeMirror typeMirror = trees.getTypeMirror(getCurrentPath());
    long startPosition = sourcePositions.getStartPosition(compilationUnitTree, tree);
    long endPosition = sourcePositions.getEndPosition(compilationUnitTree, tree);
    root = new JavacASTNode(tree, element, typeMirror, startPosition, endPosition);
    return super.visitCompilationUnit(tree, root);
  }

  @Override
  public Void visitImport(ImportTree tree, JavacASTNode parent) {
    return super.visitImport(tree, process(tree, parent));
  }

  @Override
  public Void visitClass(ClassTree tree, JavacASTNode parent) {
    return super.visitClass(tree, process(tree, parent));
  }

  @Override
  public Void visitMethod(MethodTree tree, JavacASTNode parent) {
    return super.visitMethod(tree, process(tree, parent));
  }

  @Override
  public Void visitVariable(VariableTree tree, JavacASTNode parent) {
    return super.visitVariable(tree, process(tree, parent));
  }

  @Override
  public Void visitEmptyStatement(EmptyStatementTree tree, JavacASTNode parent) {
    return super.visitEmptyStatement(tree, process(tree, parent));
  }

  @Override
  public Void visitBlock(BlockTree tree, JavacASTNode parent) {
    return super.visitBlock(tree, process(tree, parent));
  }

  @Override
  public Void visitDoWhileLoop(DoWhileLoopTree tree, JavacASTNode parent) {
    return super.visitDoWhileLoop(tree, process(tree, parent));
  }

  @Override
  public Void visitWhileLoop(WhileLoopTree tree, JavacASTNode parent) {
    return super.visitWhileLoop(tree, process(tree, parent));
  }

  @Override
  public Void visitForLoop(ForLoopTree tree, JavacASTNode parent) {
    return super.visitForLoop(tree, process(tree, parent));
  }

  @Override
  public Void visitEnhancedForLoop(EnhancedForLoopTree tree, JavacASTNode parent) {
    return super.visitEnhancedForLoop(tree, process(tree, parent));
  }

  @Override
  public Void visitLabeledStatement(LabeledStatementTree tree, JavacASTNode parent) {
    return super.visitLabeledStatement(tree, process(tree, parent));
  }

  @Override
  public Void visitSwitch(SwitchTree tree, JavacASTNode parent) {
    return super.visitSwitch(tree, process(tree, parent));
  }

  @Override
  public Void visitCase(CaseTree tree, JavacASTNode parent) {
    return super.visitCase(tree, process(tree, parent));
  }

  @Override
  public Void visitSynchronized(SynchronizedTree tree, JavacASTNode parent) {
    return super.visitSynchronized(tree, process(tree, parent));
  }

  @Override
  public Void visitTry(TryTree tree, JavacASTNode parent) {
    return super.visitTry(tree, process(tree, parent));
  }

  @Override
  public Void visitCatch(CatchTree tree, JavacASTNode parent) {
    return super.visitCatch(tree, process(tree, parent));
  }

  @Override
  public Void visitConditionalExpression(ConditionalExpressionTree tree, JavacASTNode parent) {
    return super.visitConditionalExpression(tree, process(tree, parent));
  }

  @Override
  public Void visitIf(IfTree tree, JavacASTNode parent) {
    return super.visitIf(tree, process(tree, parent));
  }

  @Override
  public Void visitExpressionStatement(ExpressionStatementTree tree, JavacASTNode parent) {
    return super.visitExpressionStatement(tree, process(tree, parent));
  }

  @Override
  public Void visitBreak(BreakTree tree, JavacASTNode parent) {
    return super.visitBreak(tree, process(tree, parent));
  }

  @Override
  public Void visitContinue(ContinueTree tree, JavacASTNode parent) {
    return super.visitContinue(tree, process(tree, parent));
  }

  @Override
  public Void visitReturn(ReturnTree tree, JavacASTNode parent) {
    return super.visitReturn(tree, process(tree, parent));
  }

  @Override
  public Void visitThrow(ThrowTree tree, JavacASTNode parent) {
    return super.visitThrow(tree, process(tree, parent));
  }

  @Override
  public Void visitAssert(AssertTree tree, JavacASTNode parent) {
    return super.visitAssert(tree, process(tree, parent));
  }

  @Override
  public Void visitMethodInvocation(MethodInvocationTree tree, JavacASTNode parent) {
    return super.visitMethodInvocation(tree, process(tree, parent));
  }

  @Override
  public Void visitNewClass(NewClassTree tree, JavacASTNode parent) {
    return super.visitNewClass(tree, process(tree, parent));
  }

  @Override
  public Void visitNewArray(NewArrayTree tree, JavacASTNode parent) {
    return super.visitNewArray(tree, process(tree, parent));
  }

  @Override
  public Void visitLambdaExpression(LambdaExpressionTree tree, JavacASTNode parent) {
    return super.visitLambdaExpression(tree, process(tree, parent));
  }

  @Override
  public Void visitParenthesized(ParenthesizedTree tree, JavacASTNode parent) {
    return super.visitParenthesized(tree, process(tree, parent));
  }

  @Override
  public Void visitAssignment(AssignmentTree tree, JavacASTNode parent) {
    return super.visitAssignment(tree, process(tree, parent));
  }

  @Override
  public Void visitCompoundAssignment(CompoundAssignmentTree tree, JavacASTNode parent) {
    return super.visitCompoundAssignment(tree, process(tree, parent));
  }

  @Override
  public Void visitUnary(UnaryTree tree, JavacASTNode parent) {
    return super.visitUnary(tree, process(tree, parent));
  }

  @Override
  public Void visitBinary(BinaryTree tree, JavacASTNode parent) {
    return super.visitBinary(tree, process(tree, parent));
  }

  @Override
  public Void visitTypeCast(TypeCastTree tree, JavacASTNode parent) {
    return super.visitTypeCast(tree, process(tree, parent));
  }

  @Override
  public Void visitInstanceOf(InstanceOfTree tree, JavacASTNode parent) {
    return super.visitInstanceOf(tree, process(tree, parent));
  }

  @Override
  public Void visitArrayAccess(ArrayAccessTree tree, JavacASTNode parent) {
    return super.visitArrayAccess(tree, process(tree, parent));
  }

  @Override
  public Void visitMemberSelect(MemberSelectTree tree, JavacASTNode parent) {
    return super.visitMemberSelect(tree, process(tree, parent));
  }

  @Override
  public Void visitMemberReference(MemberReferenceTree tree, JavacASTNode parent) {
    return super.visitMemberReference(tree, process(tree, parent));
  }

  @Override
  public Void visitIdentifier(IdentifierTree tree, JavacASTNode parent) {
    return super.visitIdentifier(tree, process(tree, parent));
  }

  @Override
  public Void visitLiteral(LiteralTree tree, JavacASTNode parent) {
    return super.visitLiteral(tree, process(tree, parent));
  }

  @Override
  public Void visitPrimitiveType(PrimitiveTypeTree tree, JavacASTNode parent) {
    return super.visitPrimitiveType(tree, process(tree, parent));
  }

  @Override
  public Void visitArrayType(ArrayTypeTree tree, JavacASTNode parent) {
    return super.visitArrayType(tree, process(tree, parent));
  }

  @Override
  public Void visitParameterizedType(ParameterizedTypeTree tree, JavacASTNode parent) {
    return super.visitParameterizedType(tree, process(tree, parent));
  }

  @Override
  public Void visitUnionType(UnionTypeTree tree, JavacASTNode parent) {
    return super.visitUnionType(tree, process(tree, parent));
  }

  @Override
  public Void visitIntersectionType(IntersectionTypeTree tree, JavacASTNode parent) {
    return super.visitIntersectionType(tree, process(tree, parent));
  }

  @Override
  public Void visitTypeParameter(TypeParameterTree tree, JavacASTNode parent) {
    return super.visitTypeParameter(tree, process(tree, parent));
  }

  @Override
  public Void visitWildcard(WildcardTree tree, JavacASTNode parent) {
    return super.visitWildcard(tree, process(tree, parent));
  }

  @Override
  public Void visitModifiers(ModifiersTree tree, JavacASTNode parent) {
    return super.visitModifiers(tree, process(tree, parent));
  }

  @Override
  public Void visitAnnotation(AnnotationTree tree, JavacASTNode parent) {
    return super.visitAnnotation(tree, process(tree, parent));
  }

  @Override
  public Void visitAnnotatedType(AnnotatedTypeTree tree, JavacASTNode parent) {
    return super.visitAnnotatedType(tree, process(tree, parent));
  }

  @Override
  public Void visitOther(Tree tree, JavacASTNode parent) {
    return super.visitOther(tree, process(tree, parent));
  }

  @Override
  public Void visitErroneous(ErroneousTree tree, JavacASTNode parent) {
    return super.visitErroneous(tree, process(tree, parent));
  }
}
