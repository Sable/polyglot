package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.visit.*;
import polyglot.ext.jl5.types.*;

public class JL5CanonicalTypeNode_c extends CanonicalTypeNode_c implements JL5CanonicalTypeNode{

    public JL5CanonicalTypeNode_c(Position pos, Type type) {
        super(pos, type);
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (type() instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType)type();
            if (pt.baseType() instanceof IntersectionType){
                throw new SemanticException("Unexpected type: only class types can have type arguments.", position());
            }
            if (!pt.typeArguments().isEmpty() && pt.typeArguments().size() != pt.typeVariables().size()){
                    throw new SemanticException("Must give type arguments for all declared type variables or none.", position());
            }
            for (int i = 0; i < pt.typeVariables().size(); i++){
                Type arg = (Type)pt.typeArguments().get(i);
                if (!tc.typeSystem().isSubtype(arg, (Type)pt.typeVariables().get(i))){
                    throw new SemanticException("Invalid type argument "+arg, position());
                }
            }
        }
        return super.typeCheck(tc);
    }

}
