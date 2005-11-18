package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5ArrayAccess_c extends ArrayAccess_c implements JL5ArrayAccess, UnboxingVisit  {

    public JL5ArrayAccess_c(Position pos, Expr array, Expr index){
        super(pos, array, index);
    }

    public Node unboxing(UnboxingVisitor v) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();
        if (index().type().isClass()){
            return index(nf.createUnboxed(index().position(), index(), ts.primitiveOf(index.type()), ts, v.context()));
        }
        return this;
    }
}
