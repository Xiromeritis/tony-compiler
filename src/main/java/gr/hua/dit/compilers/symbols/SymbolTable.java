package gr.hua.dit.compilers.symbols;

import java.util.Map;
import java.util.Stack;
import java.util.HashMap;

import gr.hua.dit.compilers.DuplicateDeclException;
import gr.hua.dit.compilers.SemanticError;
import gr.hua.dit.compilers.UndefinedVarException;
import gr.hua.dit.compilers.ast.TypeNode;

public class SymbolTable {

    private final Stack<Map<String,SymbolEntry>> scopes;

    public SymbolTable() {
        scopes = new Stack<>();
    }

    public void enterScope() { scopes.add(new HashMap<>()); }
    public void enterScope(Map<String,SymbolEntry> table) { scopes.add(table); }
    public void exitScope() { scopes.pop(); }

    public Map<String,SymbolEntry> getScope() { return scopes.peek(); }

    public void addEntry(String name, TypeNode t) throws SemanticError {
        Map<String, SymbolEntry> m = scopes.peek();
        if (!m.containsKey(name)) {
            m.put(name, new SymbolEntry(name, t));
        } else {
            throw new DuplicateDeclException("Variable " + name + " already defined");
        }
    }

    public SymbolEntry getEntry(String name) throws SemanticError {
        for ( Map<String, SymbolEntry> m : scopes.reversed()) {
            SymbolEntry e = m.get(name);
            if (e != null)
                return e;
        }
        throw new UndefinedVarException("Variable " + name + " is undefined");
    }
}
