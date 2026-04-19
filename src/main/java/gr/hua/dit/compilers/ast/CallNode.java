package gr.hua.dit.compilers.ast;
import java.util.List;

public class CallNode extends Expr {
  private final String functionName;
  private final List<Expr> arguments;

  public CallNode(String functionName, List<Expr> arguments) {
    this.functionName = functionName;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    return "Call(" + functionName + ", args: " + arguments + ")";
  }
}
