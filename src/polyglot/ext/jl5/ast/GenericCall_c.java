package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;

public class GenericCall_c extends Call_c implements GenericCall {

    protected List typeArguments;

    public GenericCall_c(Position pos, Receiver target, String name, List arguments, List typeArguments){
        super(pos, target, name, arguments);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public GenericCall typeArguments(List args){
        GenericCall_c n = (GenericCall_c) copy();
        n.typeArguments = args;
        return n;
    }
}
