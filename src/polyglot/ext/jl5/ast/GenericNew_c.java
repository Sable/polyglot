package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;

public class GenericNew_c extends JL5New_c implements GenericNew {

    protected List typeArguments;

    public GenericNew_c(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArguments){
        super(pos, qualifier, tn, arguments, body);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public GenericNew typeArguments(List args){
        GenericNew_c n = (GenericNew_c) copy();
        n.typeArguments = args;
        return n;
    }
    
}
