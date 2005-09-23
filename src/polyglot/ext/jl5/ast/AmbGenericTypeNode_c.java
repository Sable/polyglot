package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;

public class AmbGenericTypeNode_c extends AmbTypeNode_c implements AmbGenericTypeNode {

    protected List typeArguments;

    public AmbGenericTypeNode_c(Position pos, QualifierNode qual, String name, List typeArguments) {
        super(pos, qual, name);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public AmbGenericTypeNode typeArguments(List args){
        AmbGenericTypeNode_c n = (AmbGenericTypeNode_c) copy();
        n.typeArguments = args;
        return n;
    }
}
