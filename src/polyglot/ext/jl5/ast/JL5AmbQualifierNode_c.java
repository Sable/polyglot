package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;

public class JL5AmbQualifierNode_c extends AmbQualifierNode_c implements JL5AmbQualifierNode {

    protected List typeArguments;
    
    public JL5AmbQualifierNode_c(Position pos, QualifierNode qual, String name, List typeArguments){
        super(pos, qual, name);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5AmbQualifierNode typeArguments(List args){
        JL5AmbQualifierNode_c n = (JL5AmbQualifierNode_c) copy();
        n.typeArguments = args;
        return n;
    }
}
