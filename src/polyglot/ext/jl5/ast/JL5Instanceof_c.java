package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5Instanceof_c extends Instanceof_c implements JL5Instanceof {

    public JL5Instanceof_c(Position pos, Expr expr, TypeNode compareType){
        super(pos, expr, compareType);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (compareType().type() instanceof ParameterizedType){
            throw new SemanticException("Type arguments not allowed here.", compareType().position());
        }
        if (compareType().type() instanceof IntersectionType){
            throw new SemanticException("Type variable not allowed here.", compareType().position());
        }
        return super.typeCheck(tc);
    }
}
