package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public abstract class ASTNode {
  public abstract void accept(Visitor visitor);
}
