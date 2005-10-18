package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.util.*;
import java.util.*;
import polyglot.ext.jl.*;
import polyglot.ext.jl.ast.*;

/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a type and id and an expression
 * to be iterated over.
 */
public class ExtendedFor_c extends Loop_c implements ExtendedFor
{
    protected List varDecls;
    /*protected Flags flags;
    protected TypeNode type;
    protected String name;*/
    protected Expr expr;
    protected Stmt body;

    public ExtendedFor_c(Position pos, List varDecls, Expr expr, Stmt body) {
	    super(pos);
	    /*this.flags = flags;
	    this.type = type;
	    this.name = name;*/
        this.varDecls = varDecls;
        this.expr = expr;
	    this.body = body;
    }

    /*public ExtendedFor flags(Flags flags){
        ExtendedFor_c n = (ExtendedFor_c) copy();
        n.flags = flags;
        return n;
    }

    public Flags flags(){
        return flags;
    }

    public ExtendedFor type(TypeNode type){
        if (type == this.type) return this;
        ExtendedFor_c n = (ExtendedFor_c) copy();
        n.type = type;
        return n;
    }
    
    public TypeNode type(){
        return type;
    }

    public ExtendedFor name(String name){
        if (name.equals(this.name)) return this;
        ExtendedFor_c n = (ExtendedFor_c) copy();
        n.name = name;
        return n;
    }
   
    public String name(){
        return name;
    }*/
    
    /** Loop body */
    public Stmt body() {
	    return this.body;
    }

    /** Set the body of the statement. */
    public ExtendedFor body(Stmt body) {
	    ExtendedFor_c n = (ExtendedFor_c) copy();
	    n.body = body;
	    return n;
    }

    /** Reconstruct the statement. */
    protected ExtendedFor_c reconstruct(List varDecls /*Flags flags, TypeNode type, String name*/, Expr expr, Stmt body) {
	    if (! CollectionUtil.equals(varDecls, this.varDecls) /*flags != this.flags || type != this.type || !(name.equals(this.name))*/ || expr != this.expr || body != this.body) {
	        ExtendedFor_c n = (ExtendedFor_c) copy();
            /*n.flags = flags;
            n.type = type;
            n.name = name;*/
            n.varDecls = varDecls;
            n.expr = expr;
	        n.body = body;
	        return n;
	    }

	    return this;
    }

    /** Reconstruct the statement. */
    /*protected ExtendedFor_c reconstruct(List varDecls, Expr expr, Stmt body) {
	    if (!CollectionUtil.equals(varDecl, this.VarDecl) || expr != this.expr || body != this.body) {
	        ExtendedFor_c n = (ExtendedFor_c) copy();
            n.varDecls = varDecls;
            n.expr = expr;
	        n.body = body;
	        return n;
	    }

	    return this;
    }*/

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
        //TypeNode type = (TypeNode) visitChild(this.type, v);
        List varDecls = visitList(this.varDecls, v);
        Expr expr = (Expr) visitChild(this.expr, v);
	    Stmt body = (Stmt) visitChild(this.body, v);
	    return reconstruct(varDecls, expr, body);
    }

    public Context enterScope(Context c) {
	    return c.pushBlock();
    }

    /** Type check the statement. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
	    TypeSystem ts = tc.typeSystem();

        // Check that the expr is an array or of type Iterable
        Type t = expr.type();
        if (t.isArray()){
            ArrayType aType = (ArrayType)t;
            t = aType.base();
            /*if (!aType.base().isImplicitCastValid(type.type())){
                throw new SemanticException("Type mismatch in EnhancedFor, array or collection of "+type.type()+" expected.", expr.position());
            }*/
        }
        
        // Check that type is the same as elements in expr
        LocalDecl ld = (LocalDecl)varDecls.get(0);
        Type declType = ld.type().type();
        if (!ts.isImplicitCastValid(t, declType)){
            throw new SemanticException("Incompatible types: ", expr.position());
        }

        //System.out.println("expr; "+expr+" is a "+expr.getClass());
        if (expr instanceof Local && ld.localInstance().equals(((Local)expr).localInstance())){
            throw new SemanticException("Varaible: "+expr+" may not have been initialized", expr.position());
        }
        if (expr instanceof NewArray){
            if (((NewArray)expr).init() != null){
                for (Iterator it = ((NewArray)expr).init().elements().iterator(); it.hasNext(); ){
                    Expr next = (Expr)it.next();
                    if (next instanceof Local && ld.localInstance().equals(((Local)next).localInstance())){
                        throw new SemanticException("Varaible: "+next+" may not have been initialized", next.position());
                    }
                }
            }
        }
        
	    return this;
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        return child.type();
    }

    public void printVarDecl(Object decl, CodeWriter w, PrettyPrinter tr){
        if (decl instanceof LocalDecl){
            LocalDecl ld = (LocalDecl)decl;
            w.write(ld.flags().translate());
            print(ld.type(), w, tr);
            w.write(" ");
            w.write(ld.name());
        }
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("for (");
        for (Iterator it = varDecls.iterator(); it.hasNext();){
            printVarDecl(it.next(), w, tr);
        }
        /*w.write(flags.translate());
        print(type, w, tr);
        w.write(" ");
        w.write(name);*/
        w.write(" : ");
        print(expr, w, tr);
        w.write(")");
        printSubStmt(body, w, tr);
    }

    public String toString() {
	    return "for (...) ...";
    }

    public Term entry() {
        return expr;
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        v.visitCFG(expr, FlowGraph.EDGE_KEY_TRUE, body.entry(), FlowGraph.EDGE_KEY_FALSE, this);

        v.push(this).visitCFG(body, expr);
        return succs;
    }

    public Term continueTarget() {
        return body.entry();
    }

    public boolean condIsConstant(){
        return false;
    }

    public Expr cond(){
        return null;
    }
}
