package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;

public class JL5AmbExpr_c extends AmbExpr_c implements JL5AmbExpr
{


    public JL5AmbExpr_c(Position pos, String name){
        super(pos, name);
    }

    

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return this;
    }
}
