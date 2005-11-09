package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;
import java.util.*;

public class JL5NewArray_c extends NewArray_c implements JL5NewArray{

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
}
