package gr.hua.dit.compilers.ast;
import gr.hua.dit.compilers.visitors.Visitor;

import java.util.List;

public class VarDefNode extends ASTNode {
  private final TypeNode type;
  private final List<String> ids;

  public VarDefNode(TypeNode type, List<String> ids) {
    this.type = type;
    this.ids = ids;
  }

  public TypeNode getType() { return type; }

  public List<String> getIds() { return ids; }

  @Override
  public String toString() {
    String typeStr = type != null ? type.toString() : "unknown";
    return "VarDef(" + typeStr + " " + ids.toString() + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
