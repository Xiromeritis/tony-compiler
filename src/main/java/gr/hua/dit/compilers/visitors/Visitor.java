package gr.hua.dit.compilers.visitors;

import gr.hua.dit.compilers.ast.*;

public interface Visitor {
  // Structural Nodes
  void visit(ProgramNode node);
  void visit(FuncDefNode node);
  void visit(FuncDeclNode node);
  void visit(HeaderNode node);
  void visit(FormalNode node);
  void visit(VarDefNode node);
  void visit(TypeNode node);
  void visit(ArrayTypeNode node);
  void visit(ListTypeNode node);

  // Statements
  void visit(AssignStmt node);
  void visit(CallStmt node);
  void visit(ElsifStmt node);
  void visit(ExitStmt node);
  void visit(ForStmt node);
  void visit(IfStmt node);
  void visit(ReturnStmt node);
  void visit(SkipStmt node);

  // Expressions
  void visit(AddNode node);
  void visit(AndNode node);
  void visit(ArrayAccessNode node);
  void visit(BooleanNode node);
  void visit(CallNode node);
  void visit(CharNode node);
  void visit(ConsNode node);
  void visit(DivNode node);
  void visit(EqNode node);
  void visit(GeqNode node);
  void visit(GtNode node);
  void visit(HeadNode node);
  void visit(IdNode node);
  void visit(IntegerNode node);
  void visit(LeqNode node);
  void visit(LtNode node);
  void visit(ModNode node);
  void visit(MultNode node);
  void visit(NeqNode node);
  void visit(NewArrayNode node);
  void visit(NilNode node);
  void visit(NilqNode node);
  void visit(NotNode node);
  void visit(OrNode node);
  void visit(StringNode node);
  void visit(SubNode node);
  void visit(TailNode node);
  void visit(UnaryMinusNode node);
  void visit(UnaryPlusNode node);
}
