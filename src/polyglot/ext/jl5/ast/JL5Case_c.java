package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5Case_c extends Case_c implements JL5Case  {


    protected Expr target;
    
    public JL5Case_c(Position pos, Expr expr){
        super(pos, expr);
    }

    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException{
        return ar;
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        Expr target = (Expr)visitChild(this.target, v);
        return super.visitChildren(v);
    }

    public Case_c reconstruct(Expr expr){
        return (JL5Case_c)super.reconstruct(expr);
    }
    /*public Node typeCheck(TypeChecker tc) throws SemanticException {
        
    }*/
}
