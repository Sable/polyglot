package polyglot.ext.jl5.ast;

import polyglot.ext.jl.ast.*;
import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5Return_c extends Return_c implements JL5Return, UnboxingVisit, BoxingVisit {

    public JL5Return_c(Position pos, Expr expr){
        super(pos, expr);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av){
        if (child == expr){
            Context c = av.context();
            if (c.currentCode() instanceof MethodInstance){
                return ((MethodInstance)c.currentCode()).returnType();
            }
        }
        return child.type();
    }

    public Node unboxing(UnboxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        Context c = bv.context();
        
        if (c.currentCode() instanceof MethodInstance && expr != null){
            if (ts.needsUnboxing(((MethodInstance)c.currentCode()).returnType(), expr.type())){
                return expr(nf.createUnboxed(expr().position(), expr(), ((MethodInstance)c.currentCode()).returnType(), ts, c));
            }
        }        
        return this;
    }
    
    public Node boxing(BoxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        Context c = bv.context();
        
        if (c.currentCode() instanceof MethodInstance && expr != null){
            if (ts.needsBoxing(((MethodInstance)c.currentCode()).returnType(), expr.type())){
                return expr(nf.createBoxed(expr().position(), expr(), ((MethodInstance)c.currentCode()).returnType(), ts, c));
            }
        }        
        return this;
    }
}
