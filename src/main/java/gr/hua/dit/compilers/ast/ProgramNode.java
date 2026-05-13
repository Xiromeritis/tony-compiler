package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class ProgramNode extends ASTNode {
  private final FuncDefNode mainFunc;

  public ProgramNode(FuncDefNode mainFunc) {
    this.mainFunc = mainFunc;
  }

  public FuncDefNode getMainFunc() { return mainFunc; }

  @Override
  public String toString() {
    return "Program(\n  " + mainFunc.toString() + "\n)";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
