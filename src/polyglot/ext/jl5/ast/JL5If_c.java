package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5If_c extends If_c implements JL5If, UnboxingVisit {

    public JL5If_c(Position pos, Expr cond, Stmt consequent, Stmt alternative){
        super(pos, cond, consequent, alternative);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        if (!ts.equals(cond.type(), ts.Boolean()) && !ts.equals(cond.type(), ts.BooleanWrapper())){
            throw new SemanticException("Condition of if must have boolean type.", cond.position());
        }
        return this;
    }
    
    public Node unboxing(UnboxingVisitor v) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();
        if (cond.type().isClass()){
            return cond(nf.createUnboxed(cond.position(), cond(), ts.primitiveOf(cond.type()), ts, v.context()));
        }
        return this;
    }
}
