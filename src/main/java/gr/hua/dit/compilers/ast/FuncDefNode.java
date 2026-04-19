package gr.hua.dit.compilers.ast;
import java.util.List;

public class FuncDefNode extends ASTNode {
  private final HeaderNode header;
  private final List<ASTNode> localDecls; // Contains FuncDefNode, FuncDeclNode, or VarDefNode
  private final List<Stmt> stmts;

  public FuncDefNode(HeaderNode header, List<ASTNode> localDecls, List<Stmt> stmts) {
    this.header = header;
    this.localDecls = localDecls;
    this.stmts = stmts;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FuncDef(\n  ");
    sb.append(header != null ? header.toString() : "null");

    if (localDecls != null && !localDecls.isEmpty()) {
      sb.append(",\n  LocalDecls: ").append(localDecls);
    }

    if (stmts != null && !stmts.isEmpty()) {
      sb.append(",\n  Body: ").append(stmts);
    }

    sb.append("\n)");
    return sb.toString();
  }
}
