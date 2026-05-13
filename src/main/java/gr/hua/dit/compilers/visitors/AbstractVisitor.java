package gr.hua.dit.compilers.visitors;

import gr.hua.dit.compilers.ast.*;

/**
 * Base visitor providing empty implementations for all AST nodes.
 * Subclasses only override the nodes they need to process.
 */
public abstract class AbstractVisitor implements Visitor {

  // Structural Nodes
  @Override public void visit(ProgramNode node) {}
  @Override public void visit(FuncDefNode node) {}
  @Override public void visit(FuncDeclNode node) {}
  @Override public void visit(HeaderNode node) {}
  @Override public void visit(FormalNode node) {}
  @Override public void visit(VarDefNode node) {}
  @Override public void visit(TypeNode node) {}
  @Override public void visit(ArrayTypeNode node) {}
  @Override public void visit(ListTypeNode node) {}

  // Statements
  @Override public void visit(AssignStmt node) {}
  @Override public void visit(CallStmt node) {}
  @Override public void visit(ElsifStmt node) {}
  @Override public void visit(ExitStmt node) {}
  @Override public void visit(ForStmt node) {}
  @Override public void visit(IfStmt node) {}
  @Override public void visit(ReturnStmt node) {}
  @Override public void visit(SkipStmt node) {}

  // Expressions
  @Override public void visit(AddNode node) {}
  @Override public void visit(AndNode node) {}
  @Override public void visit(ArrayAccessNode node) {}
  @Override public void visit(BooleanNode node) {}
  @Override public void visit(CallNode node) {}
  @Override public void visit(CharNode node) {}
  @Override public void visit(ConsNode node) {}
  @Override public void visit(DivNode node) {}
  @Override public void visit(EqNode node) {}
  @Override public void visit(GeqNode node) {}
  @Override public void visit(GtNode node) {}
  @Override public void visit(HeadNode node) {}
  @Override public void visit(IdNode node) {}
  @Override public void visit(IntegerNode node) {}
  @Override public void visit(LeqNode node) {}
  @Override public void visit(LtNode node) {}
  @Override public void visit(ModNode node) {}
  @Override public void visit(MultNode node) {}
  @Override public void visit(NeqNode node) {}
  @Override public void visit(NewArrayNode node) {}
  @Override public void visit(NilNode node) {}
  @Override public void visit(NilqNode node) {}
  @Override public void visit(NotNode node) {}
  @Override public void visit(OrNode node) {}
  @Override public void visit(StringNode node) {}
  @Override public void visit(SubNode node) {}
  @Override public void visit(TailNode node) {}
  @Override public void visit(UnaryMinusNode node) {}
  @Override public void visit(UnaryPlusNode node) {}
}
