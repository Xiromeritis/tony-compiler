package gr.hua.dit.compilers.visitors;

import gr.hua.dit.compilers.ast.*;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CodeGenerator extends AbstractVisitor {

  private final StringBuilder code = new StringBuilder();
  private final StringBuilder globals = new StringBuilder();
  private final Stack<String> valueStack = new Stack<>();

  private final Map<String, String> varAddresses = new HashMap<>();
  private final Map<String, String> varTypes = new HashMap<>();
  private final Map<String, Boolean> isRefVar = new HashMap<>();
  private final Map<String, String> funcReturnTypes = new HashMap<>();

  private final Stack<String> expectedReturnType = new Stack<>();
  private String rootFuncName = null;

  private int tempCounter = 1;
  private int labelCounter = 1;
  private int stringCounter = 1;

  private String newTemp() { return "%t" + (tempCounter++); }
  private String newLabel() { return "L" + (labelCounter++); }

  private String getLlvmType(String typeStr) {
    if (typeStr == null) return "void";
    if (typeStr.contains("list[")) return "i8*";
    if (typeStr.contains("char[]")) return "i8*";
    if (typeStr.contains("[]")) return "i32*";
    if (typeStr.equals("int") || typeStr.equals("bool")) return "i32";
    if (typeStr.equals("char")) return "i8";
    return "void";
  }

  public void generate(ProgramNode root, String filename) {
    globals.append("declare i8* @malloc(i32)\n");
    globals.append("declare void @puti(i32)\n");
    globals.append("declare void @puts(i8*)\n");
    globals.append("declare i32 @geti()\n");
    globals.append("declare i32 @strlen(i8*)\n\n");

    funcReturnTypes.put("puti", "void");
    funcReturnTypes.put("puts", "void");
    funcReturnTypes.put("geti", "i32");
    funcReturnTypes.put("strlen", "i32");

    root.accept(this);

    try (PrintWriter out = new PrintWriter(filename)) {
      out.print(globals.toString());
      out.print(code.toString());
      System.out.println("LLVM IR generated: " + filename);
    } catch (Exception e) { e.printStackTrace(); }
  }

  @Override
  public void visit(ProgramNode node) {
    if (node.getMainFunc() != null) {
      rootFuncName = node.getMainFunc().getHeader().getName();
      node.getMainFunc().accept(this);
    }
  }

  @Override
  public void visit(FuncDefNode node) {
    if (node.getLocalDecls() != null) {
      for (ASTNode decl : node.getLocalDecls()) {
        if (decl instanceof FuncDefNode) decl.accept(this);
      }
    }

    String originalName = node.getHeader().getName();
    boolean isRoot = originalName.equals(rootFuncName);
    String llvmName = isRoot ? "main" : originalName;

    String typeStr = node.getHeader().getReturnType() != null ? node.getHeader().getReturnType().toString() : "void";
    String llvmRetType = getLlvmType(typeStr);
    if (isRoot) llvmRetType = "i32";

    funcReturnTypes.put(originalName, llvmRetType);
    expectedReturnType.push(llvmRetType);

    code.append("define ").append(llvmRetType).append(" @").append(llvmName).append("(");
    if (node.getHeader().getFormals() != null) {
      for (int i = 0; i < node.getHeader().getFormals().size(); i++) {
        FormalNode f = node.getHeader().getFormals().get(i);
        String type = getLlvmType(f.getType() != null ? f.getType().toString() : "");
        if (f.isRef() && !type.endsWith("*")) type += "*";
        for (int j = 0; j < f.getIds().size(); j++) {
          code.append(type).append(" %arg_").append(f.getIds().get(j));
          if (j < f.getIds().size() - 1 || i < node.getHeader().getFormals().size() - 1) code.append(", ");
        }
      }
    }
    code.append(") {\nentry:\n");

    if (node.getHeader().getFormals() != null) {
      for (FormalNode f : node.getHeader().getFormals()) {
        String type = getLlvmType(f.getType() != null ? f.getType().toString() : "");
        boolean isRefParam = f.isRef();
        if (isRefParam && !type.endsWith("*")) type += "*";
        for (String id : f.getIds()) {
          String reg = newTemp();
          varAddresses.put(id, reg);
          varTypes.put(id, type);
          isRefVar.put(id, isRefParam);
          code.append("  ").append(reg).append(" = alloca ").append(type).append("\n");
          code.append("  store ").append(type).append(" %arg_").append(id).append(", ").append(type).append("* ").append(reg).append("\n");
        }
      }
    }

    if (node.getLocalDecls() != null) {
      for (ASTNode decl : node.getLocalDecls()) {
        if (!(decl instanceof FuncDefNode)) decl.accept(this);
      }
    }

    if (node.getStmts() != null) {
      for (Stmt stmt : node.getStmts()) stmt.accept(this);
    }

    if (llvmRetType.equals("void")) {
      code.append("  ret void\n}\n\n");
    } else if (llvmRetType.equals("i32")) {
      code.append("  ret i32 0\n}\n\n");
    } else {
      code.append("  ret ").append(llvmRetType).append(" null\n}\n\n");
    }

    expectedReturnType.pop();
  }

  @Override
  public void visit(ReturnStmt node) {
    String retType = expectedReturnType.isEmpty() ? "void" : expectedReturnType.peek();

    if (node.getExpr() != null) {
      node.getExpr().accept(this);
      String val = valueStack.pop();
      code.append("  ret ").append(retType).append(" ").append(val).append("\n");
    } else {
      code.append("  ret void\n");
    }
    code.append("\n").append(newLabel()).append(":\n");
  }

  @Override
  public void visit(VarDefNode node) {
    for (String id : node.getIds()) {
      String reg = newTemp();
      varAddresses.put(id, reg);
      String type = getLlvmType(node.getType() != null ? node.getType().toString() : "");
      varTypes.put(id, type);
      isRefVar.put(id, false);
      code.append("  ").append(reg).append(" = alloca ").append(type).append("\n");
    }
  }

  @Override
  public void visit(AssignStmt node) {
    node.getE().accept(this);
    String rVal = valueStack.pop();

    if (node.getTarget() instanceof IdNode) {
      String id = ((IdNode) node.getTarget()).getName();
      String addr = varAddresses.get(id);
      String type = varTypes.getOrDefault(id, "i32");

      if (isRefVar.getOrDefault(id, false)) {
        String ptrReg = newTemp();
        code.append("  ").append(ptrReg).append(" = load ").append(type).append(", ").append(type).append("* ").append(addr).append("\n");
        String innerType = type.substring(0, type.length() - 1);
        code.append("  store ").append(innerType).append(" ").append(rVal).append(", ").append(innerType).append("* ").append(ptrReg).append("\n");
      } else {
        code.append("  store ").append(type).append(" ").append(rVal).append(", ").append(type).append("* ").append(addr).append("\n");
      }
    } else if (node.getTarget() instanceof ArrayAccessNode arrTarget) {
      String ptrType = "i32";
      if (arrTarget.getArray() instanceof IdNode) {
        String id = ((IdNode)arrTarget.getArray()).getName();
        if ("i8*".equals(varTypes.get(id))) ptrType = "i8";
      }
      String ptr = getArrayPointer(arrTarget, ptrType);
      if (ptrType.equals("i8")) {
        String trunc = newTemp();
        code.append("  ").append(trunc).append(" = trunc i32 ").append(rVal).append(" to i8\n");
        code.append("  store i8 ").append(trunc).append(", i8* ").append(ptr).append("\n");
      } else {
        code.append("  store i32 ").append(rVal).append(", i32* ").append(ptr).append("\n");
      }
    }
  }

  @Override
  public void visit(IdNode node) {
    String id = node.getName();
    String addr = varAddresses.get(id);
    String type = varTypes.getOrDefault(id, "i32");
    String reg = newTemp();
    code.append("  ").append(reg).append(" = load ").append(type).append(", ").append(type).append("* ").append(addr).append("\n");

    if (isRefVar.getOrDefault(id, false)) {
      String valReg = newTemp();
      String innerType = type.substring(0, type.length() - 1);
      code.append("  ").append(valReg).append(" = load ").append(innerType).append(", ").append(innerType).append("* ").append(reg).append("\n");
      valueStack.push(valReg);
    } else {
      valueStack.push(reg);
    }
  }

  @Override
  public void visit(CallNode node) {
    String funcName = node.getFunctionName();
    StringBuilder args = new StringBuilder();

    // Swap requires pass-by-reference (pointers) ---
    if (funcName.equals("swap") && node.getArguments() != null) {
      for (int i = 0; i < node.getArguments().size(); i++) {
        Expr arg = node.getArguments().get(i);
        String ptrReg = "";

        if (arg instanceof ArrayAccessNode) {
          ptrReg = getArrayPointer((ArrayAccessNode) arg, "i32");
        } else if (arg instanceof IdNode) {
          String id = ((IdNode) arg).getName();
          String addr = varAddresses.get(id);
          if (isRefVar.getOrDefault(id, false)) {
            ptrReg = newTemp();
            code.append("  ").append(ptrReg).append(" = load i32*, i32** ").append(addr).append("\n");
          } else {
            ptrReg = addr;
          }
        }
        args.append("i32* ").append(ptrReg);
        if (i < node.getArguments().size() - 1) args.append(", ");
      }
    }
    // --- STANDARD BEHAVIOR FOR ALL OTHER FUNCTIONS ---
    else if (node.getArguments() != null) {
      for (int i = 0; i < node.getArguments().size(); i++) {
        Expr arg = node.getArguments().get(i);
        arg.accept(this);
        String val = valueStack.pop();

        String type = "i32";
        if (arg instanceof IdNode) type = varTypes.getOrDefault(((IdNode)arg).getName(), "i32");
        else if (arg instanceof StringNode || arg instanceof NilNode || arg instanceof ConsNode || arg instanceof TailNode) type = "i8*";
        else if (arg instanceof CallNode) type = funcReturnTypes.getOrDefault(((CallNode)arg).getFunctionName(), "i32");

        // Force correct types for standard library
        if (funcName.equals("puts") || funcName.equals("strlen")) type = "i8*";
        else if (funcName.equals("puti")) type = "i32";

        args.append(type).append(" ").append(val);
        if (i < node.getArguments().size() - 1) args.append(", ");
      }
    }

    String retType = funcReturnTypes.getOrDefault(funcName, "void");
    if (retType.equals("void")) {
      code.append("  call void @").append(funcName).append("(").append(args.toString()).append(")\n");
    } else {
      String reg = newTemp();
      code.append("  ").append(reg).append(" = call ").append(retType).append(" @").append(funcName).append("(").append(args.toString()).append(")\n");
      valueStack.push(reg);
    }
  }

  @Override public void visit(CallStmt node) {
    if (node.getCall() != null) {
      node.getCall().accept(this);
      String ret = funcReturnTypes.getOrDefault(node.getCall().getFunctionName(), "void");
      if (!ret.equals("void") && !valueStack.isEmpty()) valueStack.pop();
    }
  }

  @Override public void visit(IntegerNode node) { valueStack.push(String.valueOf(node.getValue())); }
  @Override public void visit(BooleanNode node) { valueStack.push(node.getValue() ? "1" : "0"); }
  @Override public void visit(SkipStmt node) { }

  @Override
  public void visit(StringNode node) {
    String raw = node.getValue();
    if (raw.startsWith("\"") && raw.endsWith("\"")) {
      raw = raw.substring(1, raw.length() - 1);
    }

    StringBuilder llvmStr = new StringBuilder();
    int byteCount = 0;

    for (int i = 0; i < raw.length(); i++) {
      char c = raw.charAt(i);
      if (c == '\\' && i + 1 < raw.length()) {
        char next = raw.charAt(i + 1);
        switch (next) {
          case 'n': llvmStr.append("\\0A"); break;
          case 't': llvmStr.append("\\09"); break;
          case 'r': llvmStr.append("\\0D"); break;
          case '0': llvmStr.append("\\00"); break;
          case '"': llvmStr.append("\\22"); break;
          case '\'': llvmStr.append("\\27"); break;
          case '\\': llvmStr.append("\\5C"); break;
          default: llvmStr.append("\\").append(next); break;
        }
        i++;
      } else {
        llvmStr.append(c);
      }
      byteCount++;
    }

    llvmStr.append("\\00");
    byteCount++;

    String strId = "@.str." + (stringCounter++);
    globals.append(strId).append(" = private unnamed_addr constant [")
      .append(byteCount).append(" x i8] c\"").append(llvmStr.toString()).append("\"\n");

    String ptrReg = newTemp();
    code.append("  ").append(ptrReg).append(" = getelementptr [").append(byteCount)
      .append(" x i8], [").append(byteCount).append(" x i8]* ").append(strId)
      .append(", i32 0, i32 0\n");

    valueStack.push(ptrReg);
  }

  @Override
  public void visit(CharNode node) {
    valueStack.push(String.valueOf((int) node.getValue()));
  }

  @Override
  public void visit(UnaryMinusNode node) {
    node.getExpr().accept(this);
    String val = valueStack.pop();
    String reg = newTemp();
    code.append("  ").append(reg).append(" = sub i32 0, ").append(val).append("\n");
    valueStack.push(reg);
  }

  @Override public void visit(UnaryPlusNode node) { node.getExpr().accept(this); }

  @Override public void visit(AddNode node) { emitMath("add", node); }
  @Override public void visit(SubNode node) { emitMath("sub", node); }
  @Override public void visit(MultNode node) { emitMath("mul", node); }
  @Override public void visit(DivNode node) { emitMath("sdiv", node); }
  @Override public void visit(ModNode node) { emitMath("srem", node); }

  private void emitMath(String op, BinaryNode node) {
    node.getLeft().accept(this);
    String l = valueStack.pop();
    node.getRight().accept(this);
    String r = valueStack.pop();
    String reg = newTemp();
    code.append("  ").append(reg).append(" = ").append(op).append(" i32 ").append(l).append(", ").append(r).append("\n");
    valueStack.push(reg);
  }

  @Override public void visit(LtNode node) { emitComp("slt", node); }
  @Override public void visit(GtNode node) { emitComp("sgt", node); }
  @Override public void visit(LeqNode node) { emitComp("sle", node); }
  @Override public void visit(GeqNode node) { emitComp("sge", node); }
  @Override public void visit(EqNode node) { emitComp("eq", node); }
  @Override public void visit(NeqNode node) { emitComp("ne", node); }

  private void emitComp(String op, BinaryNode node) {
    node.getLeft().accept(this);
    String l = valueStack.pop();
    node.getRight().accept(this);
    String r = valueStack.pop();

    boolean isPtrComp = l.startsWith("%") && !l.matches(".*\\d$") && r.equals("null");
    if (isPtrComp || op.equals("eq") && r.equals("null")) {
      String condReg = newTemp();
      code.append("  ").append(condReg).append(" = icmp eq i8* ").append(l).append(", null\n");
      String z = newTemp();
      code.append("  ").append(z).append(" = zext i1 ").append(condReg).append(" to i32\n");
      valueStack.push(z);
      return;
    }

    String condReg = newTemp();
    code.append("  ").append(condReg).append(" = icmp ").append(op).append(" i32 ").append(l).append(", ").append(r).append("\n");
    String z = newTemp();
    code.append("  ").append(z).append(" = zext i1 ").append(condReg).append(" to i32\n");
    valueStack.push(z);
  }

  @Override public void visit(AndNode node) { emitLogical("and", node); }
  @Override public void visit(OrNode node) { emitLogical("or", node); }

  private void emitLogical(String op, BinaryNode node) {
    node.getLeft().accept(this);
    String l = valueStack.pop();
    node.getRight().accept(this);
    String r = valueStack.pop();

    String c1 = newTemp();
    code.append("  ").append(c1).append(" = icmp ne i32 ").append(l).append(", 0\n");
    String c2 = newTemp();
    code.append("  ").append(c2).append(" = icmp ne i32 ").append(r).append(", 0\n");

    String res = newTemp();
    code.append("  ").append(res).append(" = ").append(op).append(" i1 ").append(c1).append(", ").append(c2).append("\n");

    String finalReg = newTemp();
    code.append("  ").append(finalReg).append(" = zext i1 ").append(res).append(" to i32\n");
    valueStack.push(finalReg);
  }

  @Override
  public void visit(NotNode node) {
    node.getExpr().accept(this);
    String val = valueStack.pop();
    String reg = newTemp();
    code.append("  ").append(reg).append(" = xor i32 ").append(val).append(", 1\n");
    valueStack.push(reg);
  }

  @Override
  public void visit(NewArrayNode node) {
    node.getExpr().accept(this);
    String sizeVal = valueStack.pop();

    String elemType = node.getType() != null ? node.getType().toString() : "";
    boolean isChar = elemType.contains("char");

    String bytesVal = sizeVal;
    if (!isChar) {
      bytesVal = newTemp();
      code.append("  ").append(bytesVal).append(" = mul i32 ").append(sizeVal).append(", 4\n");
    }

    String ptrI8 = newTemp();
    code.append("  ").append(ptrI8).append(" = call i8* @malloc(i32 ").append(bytesVal).append(")\n");

    if (isChar) {
      valueStack.push(ptrI8);
    } else {
      String ptrI32 = newTemp();
      code.append("  ").append(ptrI32).append(" = bitcast i8* ").append(ptrI8).append(" to i32*\n");
      valueStack.push(ptrI32);
    }
  }

  private String getArrayPointer(ArrayAccessNode node, String ptrType) {
    node.getArray().accept(this);
    String arrPtr = valueStack.pop();
    node.getIndex().accept(this);
    String idx = valueStack.pop();
    String elemPtr = newTemp();
    code.append("  ").append(elemPtr).append(" = getelementptr ").append(ptrType).append(", ").append(ptrType).append("* ").append(arrPtr).append(", i32 ").append(idx).append("\n");
    return elemPtr;
  }

  @Override
  public void visit(ArrayAccessNode node) {
    String ptrType = "i32";
    if (node.getArray() instanceof IdNode) {
      String id = ((IdNode)node.getArray()).getName();
      if ("i8*".equals(varTypes.get(id))) ptrType = "i8";
    }
    String ptr = getArrayPointer(node, ptrType);

    if (ptrType.equals("i8")) {
      String reg = newTemp();
      code.append("  ").append(reg).append(" = load i8, i8* ").append(ptr).append("\n");
      String zext = newTemp();
      code.append("  ").append(zext).append(" = zext i8 ").append(reg).append(" to i32\n");
      valueStack.push(zext);
    } else {
      String reg = newTemp();
      code.append("  ").append(reg).append(" = load i32, i32* ").append(ptr).append("\n");
      valueStack.push(reg);
    }
  }

  @Override public void visit(NilNode node) { valueStack.push("null"); }

  @Override
  public void visit(ConsNode node) {
    node.getLeft().accept(this);
    String head = valueStack.pop();
    node.getRight().accept(this);
    String tail = valueStack.pop();

    String ptrI8 = newTemp();
    code.append("  ").append(ptrI8).append(" = call i8* @malloc(i32 16)\n");

    String headPtr = newTemp();
    code.append("  ").append(headPtr).append(" = bitcast i8* ").append(ptrI8).append(" to i32*\n");
    code.append("  store i32 ").append(head).append(", i32* ").append(headPtr).append("\n");

    String tailOffset = newTemp();
    code.append("  ").append(tailOffset).append(" = getelementptr i8, i8* ").append(ptrI8).append(", i32 8\n");
    String tailPtr = newTemp();
    code.append("  ").append(tailPtr).append(" = bitcast i8* ").append(tailOffset).append(" to i8**\n");
    code.append("  store i8* ").append(tail).append(", i8** ").append(tailPtr).append("\n");

    valueStack.push(ptrI8);
  }

  @Override
  public void visit(HeadNode node) {
    node.getExpr().accept(this);
    String listPtr = valueStack.pop();
    String headPtr = newTemp();
    code.append("  ").append(headPtr).append(" = bitcast i8* ").append(listPtr).append(" to i32*\n");
    String headVal = newTemp();
    code.append("  ").append(headVal).append(" = load i32, i32* ").append(headPtr).append("\n");
    valueStack.push(headVal);
  }

  @Override
  public void visit(TailNode node) {
    node.getExpr().accept(this);
    String listPtr = valueStack.pop();
    String tailOffset = newTemp();
    code.append("  ").append(tailOffset).append(" = getelementptr i8, i8* ").append(listPtr).append(", i32 8\n");
    String tailPtr = newTemp();
    code.append("  ").append(tailPtr).append(" = bitcast i8* ").append(tailOffset).append(" to i8**\n");
    String tailVal = newTemp();
    code.append("  ").append(tailVal).append(" = load i8*, i8** ").append(tailPtr).append("\n");
    valueStack.push(tailVal);
  }

  @Override
  public void visit(NilqNode node) {
    node.getExpr().accept(this);
    String listPtr = valueStack.pop();
    String cond = newTemp();
    code.append("  ").append(cond).append(" = icmp eq i8* ").append(listPtr).append(", null\n");
    String zext = newTemp();
    code.append("  ").append(zext).append(" = zext i1 ").append(cond).append(" to i32\n");
    valueStack.push(zext);
  }

  @Override
  public void visit(IfStmt node) {
    String labelTrue = newLabel();
    String labelFalse = newLabel();
    String labelEnd = newLabel();

    boolean hasElsifs = node.getElsifStmts() != null && !node.getElsifStmts().isEmpty();
    boolean hasElse = node.getElseBody() != null && !node.getElseBody().isEmpty();

    node.getCond().accept(this);
    String condVal = valueStack.pop();
    String condI1 = newTemp();
    code.append("  ").append(condI1).append(" = icmp ne i32 ").append(condVal).append(", 0\n");

    String nextLabel = hasElsifs ? newLabel() : (hasElse ? labelFalse : labelEnd);
    code.append("  br i1 ").append(condI1).append(", label %").append(labelTrue).append(", label %").append(nextLabel).append("\n");

    code.append("\n").append(labelTrue).append(":\n");
    if (node.getIfBody() != null) {
      for (Stmt s : node.getIfBody()) s.accept(this);
    }
    code.append("  br label %").append(labelEnd).append("\n");

    if (hasElsifs) {
      for (int i = 0; i < node.getElsifStmts().size(); i++) {
        ElsifStmt elsif = node.getElsifStmts().get(i);
        code.append("\n").append(nextLabel).append(":\n");

        elsif.getCond().accept(this);
        String eCondVal = valueStack.pop();
        String eCondI1 = newTemp();
        code.append("  ").append(eCondI1).append(" = icmp ne i32 ").append(eCondVal).append(", 0\n");

        String eLabelTrue = newLabel();
        boolean isLast = (i == node.getElsifStmts().size() - 1);
        nextLabel = isLast ? (hasElse ? labelFalse : labelEnd) : newLabel();

        code.append("  br i1 ").append(eCondI1).append(", label %").append(eLabelTrue).append(", label %").append(nextLabel).append("\n");

        code.append("\n").append(eLabelTrue).append(":\n");
        if (elsif.getBody() != null) {
          for (Stmt s : elsif.getBody()) s.accept(this);
        }
        code.append("  br label %").append(labelEnd).append("\n");
      }
    }

    if (hasElse) {
      code.append("\n").append(labelFalse).append(":\n");
      for (Stmt s : node.getElseBody()) s.accept(this);
      code.append("  br label %").append(labelEnd).append("\n");
    }

    code.append("\n").append(labelEnd).append(":\n");
  }

  @Override
  public void visit(ForStmt node) {
    String labelCond = newLabel();
    String labelBody = newLabel();
    String labelStep = newLabel();
    String labelEnd = newLabel();

    if (node.getInitList() != null) {
      for (Stmt s : node.getInitList()) s.accept(this);
    }
    code.append("  br label %").append(labelCond).append("\n");

    code.append("\n").append(labelCond).append(":\n");
    node.getCond().accept(this);
    String condVal = valueStack.pop();
    String condI1 = newTemp();
    code.append("  ").append(condI1).append(" = icmp ne i32 ").append(condVal).append(", 0\n");
    code.append("  br i1 ").append(condI1).append(", label %").append(labelBody).append(", label %").append(labelEnd).append("\n");

    code.append("\n").append(labelBody).append(":\n");
    if (node.getBody() != null) {
      for (Stmt s : node.getBody()) s.accept(this);
    }
    code.append("  br label %").append(labelStep).append("\n");

    code.append("\n").append(labelStep).append(":\n");
    if (node.getSteps() != null) {
      for (Stmt s : node.getSteps()) s.accept(this);
    }
    code.append("  br label %").append(labelCond).append("\n");

    code.append("\n").append(labelEnd).append(":\n");
  }
}
