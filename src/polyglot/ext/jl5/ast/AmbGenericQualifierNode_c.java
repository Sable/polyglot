package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;

public class AmbGenericQualifierNode_c extends AmbQualifierNode_c implements AmbGenericQualifierNode {

    protected List typeArguments;
    
    public AmbGenericQualifierNode_c(Position pos, QualifierNode qual, String name, List typeArguments){
        super(pos, qual, name);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public AmbGenericQualifierNode typeArguments(List args){
        AmbGenericQualifierNode_c n = (AmbGenericQualifierNode_c) copy();
        n.typeArguments = args;
        return n;
    }
}
