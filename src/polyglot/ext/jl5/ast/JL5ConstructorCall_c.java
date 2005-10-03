package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;

public class JL5ConstructorCall_c extends ConstructorCall_c implements JL5ConstructorCall {

    protected List typeArguments;

    public JL5ConstructorCall_c(Position pos, Kind kind, Expr qualifier, List arguments, List typeArguments){
        super(pos, kind, qualifier, arguments);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5ConstructorCall typeArguments(List args){
        JL5ConstructorCall_c n = (JL5ConstructorCall_c) copy();
        n.typeArguments = args;
        return n;
    }
}
