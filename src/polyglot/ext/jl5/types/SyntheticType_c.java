package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.ext.jl.types.*;
import java.util.*;

public class SyntheticType_c extends Type_c implements SyntheticType{

    protected List bounds;
    
    public SyntheticType_c(TypeSystem ts, List bounds){
        super(ts, null);
        this.bounds = bounds;
    }

    public List bounds(){
        return bounds;
    }
    
    public String translate(Resolver c){
        return "Synthetic Type";
    }

    public String toString(){
        return "Synthetic Type";
    }

}
