package gr.hua.dit.compilers.ast;
import gr.hua.dit.compilers.visitors.Visitor;

import java.util.List;

public class CallNode extends Expr {
  private final String functionName;
  private final List<Expr> arguments;

  public CallNode(String functionName, List<Expr> arguments) {
    this.functionName = functionName;
    this.arguments = arguments;
  }

  public String getFunctionName() { return functionName; }
  public List<Expr> getArguments() { return arguments; }

  @Override
  public String toString() {
    return "Call(" + functionName + ", args: " + arguments + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
