package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import java.util.*;

public class JL5AmbTypeNode_c extends AmbTypeNode_c implements JL5AmbTypeNode {

    protected List typeArguments;

    public JL5AmbTypeNode_c(Position pos, QualifierNode qual, String name, List typeArguments) {
        super(pos, qual, name);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5AmbTypeNode typeArguments(List args){
        JL5AmbTypeNode_c n = (JL5AmbTypeNode_c) copy();
        n.typeArguments = args;
        return n;
    }
}
