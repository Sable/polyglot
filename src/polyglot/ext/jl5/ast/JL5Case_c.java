package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5Case_c extends Case_c implements JL5Case  {

    public JL5Case_c(Position pos, Expr expr){
        super(pos, expr);
    }

    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException{
        System.out.println("disamb enter for jl5 case");
        System.out.println("expr: "+expr+" is a: "+expr.getClass());
        return ar;
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        System.out.println("disamb for jl5 case: expr: "+expr+" is a: "+expr.getClass());
        return this;
    }
}
