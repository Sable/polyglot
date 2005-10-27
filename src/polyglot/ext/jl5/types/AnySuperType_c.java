package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import java.util.*;

public class AnySuperType_c extends Type_c implements AnySuperType{

    protected Type bound;
    
    public AnySuperType_c(TypeSystem ts, Type bound){
        super(ts);
        this.bound = bound;
    }

    public Type bound(){
        return bound;
    }
    
    public String translate(Resolver c){
        return "? super "+bound.translate(c);
    }

    public String toString(){
        return "? super "+bound.toString();
    }
    
}
