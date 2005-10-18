package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;

public class AnyType_c extends Type_c implements AnyType{

    public AnyType_c(TypeSystem ts){
        super(ts, null);
    }

    public String translate(Resolver c){
        return "?";
    }

    public String toString(){
        return "?";
    }
}
