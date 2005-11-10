package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.util.*;
import java.util.*;
import polyglot.ext.jl.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a type and id and an expression
 * to be iterated over.
 */
public class ExtendedFor_c extends Loop_c implements ExtendedFor, SimplifyVisit
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

    public Node simplify(SimplifyVisitor sv) throws SemanticException{
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();
        
        Type t = expr.type();
        if (t.isArray()){
            JL5LocalDecl origLd = (JL5LocalDecl)varDecls.get(0);
            FlagAnnotations fl = new FlagAnnotations();
            fl.classicFlags(origLd.flags());
            fl.annotations(origLd.annotations());
            JL5LocalDecl ld = nf.JL5LocalDecl(position(), fl, nf.CanonicalTypeNode(position(), t.toArray().base()), "$arg0", nf.IntLit(position(), polyglot.ast.IntLit.INT, 0));
            LocalInstance li = ts.localInstance(position(), Flags.NONE, t.toArray().base(), "$arg0");
            ld = (JL5LocalDecl)ld.localInstance(li);
            Local local = nf.Local(position(), "$arg0");
            local = local.localInstance(li);
            JL5Field field = nf.JL5Field(position(), expr, "length");
            FieldInstance fi = ts.fieldInstance(position(), t.toReference(), Flags.NONE, ts.Int(), "length");
            field = (JL5Field)field.fieldInstance(fi);
            Binary cond = nf.Binary(position(), local, polyglot.ast.Binary.LT, field);
            cond = (Binary)cond.type(ts.Boolean());
            Unary iter = nf.Unary(position(), polyglot.ast.Unary.POST_INC, local);
            List iters = new ArrayList();
            iters.add(nf.Eval(position(), iter));
            
            ArrayAccess aa = nf.ArrayAccess(position(), expr, local);
            aa = (ArrayAccess)aa.type(expr.type());
            Local orig = nf.Local(position(), origLd.name());
            orig = orig.localInstance(origLd.localInstance());
            
            LocalAssign la = nf.LocalAssign(position(), orig, Assign.ASSIGN, aa);
            Block b = null;
            if (body() instanceof Block){
                b = ((Block)body()).prepend(nf.Eval(position(), la)).prepend(origLd);
            }
            else {
                b = nf.Block(position()).prepend(body()).prepend(nf.Eval(position(), la)).prepend(origLd);
            }
           
            ArrayList inits = new ArrayList();
            inits.add(ld);
            return nf.For(position(), inits, cond, iters, b);
        }
        else if (ts.isSubtype(t, ts.Iterable())){
            JL5LocalDecl origLd = (JL5LocalDecl)varDecls.get(0);
            FlagAnnotations fl = new FlagAnnotations();
            fl.classicFlags(origLd.flags());
            fl.annotations(origLd.annotations());
            
            JL5Call initCall = (JL5Call)nf.JL5Call(position(), expr, "iterator", new ArrayList(), new ArrayList());
            initCall = (JL5Call)initCall.type(ts.Iterator());
            initCall = (JL5Call)initCall.methodInstance(ts.methodInstance(position(), expr.type().toReference(), Flags.NONE, ts.Iterator(), "iterator", new ArrayList(), new ArrayList()));
            JL5LocalDecl ld = nf.JL5LocalDecl(position(), fl, nf.CanonicalTypeNode(position(), ts.Iterator()), "$arg0", initCall);
            LocalInstance li = ts.localInstance(position(), Flags.NONE, ts.Iterator(), "$arg0");
            ld = (JL5LocalDecl)ld.localInstance(li);
            Local local = nf.Local(position(), "$arg0");
            local = local.localInstance(li);
            
            JL5Call condCall = (JL5Call)nf.JL5Call(position(), local, "hasNext", new ArrayList(), new ArrayList());
            condCall = (JL5Call)condCall.type(ts.Boolean());
            condCall = (JL5Call)condCall.methodInstance(ts.methodInstance(position(), ts.Iterator(), Flags.NONE, ts.Boolean(), "hasNext", new ArrayList(), new ArrayList()));
            
            JL5Call resultCall = (JL5Call)nf.JL5Call(position(), local, "next", new ArrayList(), new ArrayList());
            resultCall = (JL5Call)resultCall.type(ts.Object());
            resultCall = (JL5Call)resultCall.methodInstance(ts.methodInstance(position(), ts.Iterator(), Flags.NONE, ts.Object(), "next", new ArrayList(),new ArrayList()));
            
            Expr assignExpr = resultCall;
            
            Local orig = nf.Local(position(), origLd.name());
            orig = orig.localInstance(origLd.localInstance());
            
            if (!ts.equals(orig.localInstance().type(), ts.Object())){
                Cast cast = nf.Cast(position(), nf.CanonicalTypeNode(position(), orig.localInstance().type()), resultCall);
                cast = (Cast)cast.type(orig.localInstance().type());
                assignExpr = cast;
            }
            LocalAssign la = nf.LocalAssign(position(), orig, Assign.ASSIGN, assignExpr);
            Block b = null;
            if (body() instanceof Block){
                b = ((Block)body()).prepend(nf.Eval(position(), la)).prepend(origLd);
            }
            else {
                b = nf.Block(position()).prepend(body()).prepend(nf.Eval(position(), la)).prepend(origLd);
            }
           
            ArrayList inits = new ArrayList();
            inits.add(ld);
            
            return nf.For(position(), inits, condCall, new ArrayList(), b);
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
