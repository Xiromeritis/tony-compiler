package gr.hua.dit.compilers.ast;
import java.util.List;

public class FormalNode extends ASTNode {
  private final boolean isRef;
  private final TypeNode type;
  private final List<String> ids;

  public FormalNode(boolean isRef, TypeNode type, List<String> ids) {
    this.isRef = isRef;
    this.type = type;
    this.ids = ids;
  }

  @Override
  public String toString() {
    String refStr = isRef ? "ref " : "";
    String typeStr = type != null ? type.toString() : "unknown";
    return "Formal(" + refStr + typeStr + " " + ids.toString() + ")";
  }
}
