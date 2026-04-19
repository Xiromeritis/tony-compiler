package gr.hua.dit.compilers.ast;
import java.util.List;

public class ElsifStmt extends ASTNode {
  private final Expr cond;
  private final List<Stmt> body;

  public ElsifStmt(Expr cond, List<Stmt> body) {
    this.cond = cond;
    this.body = body;
  }

  @Override
  public String toString() {
    return "Elsif(cond: " + cond + ", body: " + body + ")";
  }
}
