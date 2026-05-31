package gr.hua.dit.compilers.ast;
import gr.hua.dit.compilers.visitors.Visitor;

import java.util.List;

public class ElsifStmt extends ASTNode {
  private final Expr cond;
  private final List<Stmt> body;

  public ElsifStmt(Expr cond, List<Stmt> body) {
    this.cond = cond;
    this.body = body;
  }

  public Expr getCond() { return cond; }
  public List<Stmt> getBody() { return body; }

  @Override
  public String toString() {
    return "Elsif(cond: " + cond + ", body: " + body + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
