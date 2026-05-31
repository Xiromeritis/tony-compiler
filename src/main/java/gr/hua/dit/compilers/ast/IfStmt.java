package gr.hua.dit.compilers.ast;
import gr.hua.dit.compilers.visitors.Visitor;

import java.util.List;

public class IfStmt extends Stmt {
  private final Expr cond;
  private final List<Stmt> ifBody;
  private final List<ElsifStmt> elsifStmts;
  private final List<Stmt> elseBody; // Can be null

  public IfStmt(Expr cond, List<Stmt> ifBody, List<ElsifStmt> elsifStmts, List<Stmt> elseBody) {
    this.cond = cond;
    this.ifBody = ifBody;
    this.elsifStmts = elsifStmts;
    this.elseBody = elseBody;
  }

  public Expr getCond() { return cond; }
  public List<Stmt> getIfBody() { return ifBody; }
  public List<ElsifStmt> getElsifStmts() { return elsifStmts; }
  public List<Stmt> getElseBody() { return elseBody; }

  @Override
  public String toString() {
    return "If(cond: " + cond + ", body: " + ifBody +
      (elsifStmts != null && !elsifStmts.isEmpty() ? ", elsifs: " + elsifStmts : "") +
      (elseBody != null ? ", else: " + elseBody : "") + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
