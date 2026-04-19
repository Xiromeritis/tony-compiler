package gr.hua.dit.compilers.ast;

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
}
