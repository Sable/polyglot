package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.ext.jl.ast.*;
import polyglot.visit.*;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;
/**
 * An immutable representation of a Java language extended <code>for</code>
 * statement.  Contains a statement to be executed and an expression
 * to be tested indicating whether to reexecute the statement.
 */
public class EnumConstant_c extends Term_c implements EnumConstant
{   
    protected List args;
    protected String name;
    protected ClassBody body;

    public EnumConstant_c(Position pos, String name, List args, ClassBody body){
        super(pos);
        this.name = name;
        this.args = args;
        this.body = body;
    }
        
    
    /** get args */
    public List args(){
        return args;
    }

    /** set args */
    public EnumConstant args(List args){
        EnumConstant_c n = (EnumConstant_c)copy();
        n.args = args;
        return n;
    }

    /** set name */
    public EnumConstant name(String name){
        EnumConstant_c n = (EnumConstant_c)copy();
        n.name = name;
        return n;
    }

    /** get name */
    public String name(){
        return name;
    }
    
    /** set body */
    public EnumConstant body(ClassBody body){
        EnumConstant_c n = (EnumConstant_c)copy();
        n.body = body;
        return n;
    }

    /** get body */
    public ClassBody body(){
        return body;
    }

    protected EnumConstant_c reconstruct(List args, ClassBody body){
        if (!CollectionUtil.equals(args, this.args) || body != this.body) {
            EnumConstant_c n = (EnumConstant_c) copy();
            n.args = TypedList.copyAndCheck(args, Expr.class, true);
            n.body = body;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        List args = visitList(this.args, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        return reconstruct(args, body);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return this;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        w.write(name);
        if (args != null){
            w.write(" ( ");
            Iterator it = args.iterator();
            while (it.hasNext()){
                Expr e = (Expr) it.next();
                print(e, w, tr);
                if (it.hasNext()){
                    w.write(", ");
                    w.allowBreak(0);
                }
            }
            w.write(" )");
        }
        if (body != null){
            w.write(" {");
            print(body, w, tr);
            w.write("}");
        }
    }

    public List acceptCFG(CFGBuilder v, List succs){
        return succs;
    }

    public Term entry(){
        return this;
    }

    public NodeVisitor addMembersEnter(AddMemberVisitor am){
        JL5ParsedClassType ct = (JL5ParsedClassType_c)am.context().currentClassScope();

        ct.addEnumConstant(this);
        return am.bypassChildren(this);
    }
}
