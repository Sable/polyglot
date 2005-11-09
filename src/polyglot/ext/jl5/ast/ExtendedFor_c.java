package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.util.*;
import java.util.*;
import polyglot.ext.jl.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;

/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a type and id and an expression
 * to be iterated over.
 */
public class ExtendedFor_c extends Loop_c implements ExtendedFor
{
    protected List varDecls;
    protected Expr expr;
    protected Stmt body;

    public ExtendedFor_c(Position pos, List varDecls, Expr expr, Stmt body) {
	    super(pos);
        this.varDecls = varDecls;
        this.expr = expr;
	    this.body = body;
    }

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
    protected ExtendedFor_c reconstruct(List varDecls, Expr expr, Stmt body) {
	    if (! CollectionUtil.equals(varDecls, this.varDecls) || expr != this.expr || body != this.body) {
	        ExtendedFor_c n = (ExtendedFor_c) copy();
            n.varDecls = varDecls;
            n.expr = expr;
	        n.body = body;
	        return n;
	    }

	    return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
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
	    JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();

        // Check that the expr is an array or of type Iterable
        Type t = expr.type();
        if (t.isArray()){
            ArrayType aType = (ArrayType)t;
            t = aType.base();
        }
        else if (ts.isSubtype(t, ts.Iterable())){
            if (t instanceof ParameterizedType){
                t = (Type)((ParameterizedType)t).typeArguments().get(0);
            }
            else {
                t = ts.Object();
            }
        }
        
        // Check that type is the same as elements in expr
        LocalDecl ld = (LocalDecl)varDecls.get(0);
        Type declType = ld.type().type();
        if (!ts.isImplicitCastValid(t, declType)){
            throw new SemanticException("Incompatible types: ", expr.position());
        }

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
