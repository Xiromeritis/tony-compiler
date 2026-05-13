package gr.hua.dit.compilers.symbols;

import gr.hua.dit.compilers.ast.TypeNode;

public class SymbolEntry {
    public String symbolName;
    public TypeNode type;

    public SymbolEntry(String name, TypeNode typ) {
        symbolName = name;
        type = typ;
    }
}
