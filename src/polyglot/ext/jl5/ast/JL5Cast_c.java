package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5Cast_c extends Cast_c implements JL5Cast, BoxingVisit, UnboxingVisit {

    public JL5Cast_c(Position pos, TypeNode castType, Expr expr){
        super(pos, castType, expr);
    }

    public Node boxing(BoxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        if (ts.needsBoxing(this.castType().type(), expr.type())){
            return expr(nf.createBoxed(expr.position(), expr, castType().type(), ts, sv.context()));
        }
        return this;
                
    }
    
    public Node unboxing(UnboxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        if (ts.needsUnboxing(this.castType().type(), expr.type())){
            return expr(nf.createUnboxed(expr.position(), expr, castType().type(), ts, sv.context()));
        }
        return this;
                
    }

    // the original of this method makes all numerics be doubles
    // which is bad
    public Type childExpectedType(Expr child, AscriptionVisitor av){
        TypeSystem ts = av.typeSystem();
    
        return child.type();
        /*if (child == expr){
            if (castType.type().isReference()){
                return ts.Object();
            }
        }*/
    }
}
