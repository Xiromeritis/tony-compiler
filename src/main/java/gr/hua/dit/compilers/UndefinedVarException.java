package gr.hua.dit.compilers;

public class UndefinedVarException extends SemanticError {
    public UndefinedVarException(String msg) {
        super(msg);
    }
}
