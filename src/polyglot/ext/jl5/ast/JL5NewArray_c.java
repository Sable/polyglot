package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import java.util.*;

public class JL5NewArray_c extends NewArray_c implements JL5NewArray, UnboxingVisit, BoxingVisit{

    public JL5NewArray_c(Position pos, TypeNode baseType, List dims, int addDims, ArrayInit init){
        super(pos, baseType, dims, addDims, init);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Type t = baseType.type();
        if (t instanceof IntersectionType){
            throw new SemanticException("Generic array creation.", baseType.position());
        }
        if (t instanceof ParameterizedType){
            for (Iterator it = ((ParameterizedType)t).typeArguments().iterator(); it.hasNext(); ){
                Type next = (Type)it.next();
                if (!(next instanceof AnyType)){
                    throw new SemanticException("Generic array creation.", baseType.position());
                }
            }
        }
        if (t instanceof ClassType && ((ClassType)t).isNested()){
            Type outer = ((ClassType)t).outer();
            while (outer != null){
                if (outer instanceof JL5ParsedClassType && ((JL5ParsedClassType)outer).isGeneric()){
                    throw new SemanticException("Generic array creation.", baseType.position());
                }
                outer = ((ClassType)outer).outer(); 
            }
        }
        return super.typeCheck(tc);
    }

    public Node unboxing(UnboxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        if (init != null){
            ArrayList list = new ArrayList();
            for (Iterator it = init.elements().iterator(); it.hasNext(); ){
                Expr next = (Expr)it.next();
                if (ts.needsUnboxing(baseType().type(), next.type())){
                    list.add(nf.createUnboxed(next.position(), next, baseType().type(), ts, bv.context()));
                }
                else {
                    list.add(next);
                }
            }
            return init(init().elements(list));
        }
        return this;
    }
    
    public Node boxing(BoxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        if (init() != null){
            ArrayList list = new ArrayList();
            for (Iterator it = init().elements().iterator(); it.hasNext(); ){
                Expr next = (Expr)it.next();
                if (ts.needsBoxing(baseType().type(), next.type())){
                    list.add(nf.createBoxed(next.position(), next, baseType().type(), ts, bv.context()));
                }
                else {
                    list.add(next);
                }
            }
            return init(init().elements(list));
        }
        return this;
    }
}
