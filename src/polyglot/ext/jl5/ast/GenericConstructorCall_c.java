package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;

public class GenericConstructorCall_c extends ConstructorCall_c implements GenericConstructorCall {

    protected List typeArguments;

    public GenericConstructorCall_c(Position pos, Kind kind, Expr qualifier, List arguments, List typeArguments){
        super(pos, kind, qualifier, arguments);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public GenericConstructorCall typeArguments(List args){
        GenericConstructorCall_c n = (GenericConstructorCall_c) copy();
        n.typeArguments = args;
        return n;
    }
}
