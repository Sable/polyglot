package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.util.*;

public class GenericParsedClassType_c extends JL5ParsedClassType_c implements GenericParsedClassType {

    protected List typeVariables;
   
    public GenericParsedClassType_c(TypeSystem ts, LazyClassInitializer init, Source fromSource){
        super(ts, init, fromSource);
    }
    
    public List typeVariables(){
        return typeVariables;
    }
    
    public void addTypeVariable(IntersectionType type){
        if (typeVariables == null){
            typeVariables = new TypedList(new LinkedList(), IntersectionType.class, false);
        }
        typeVariables.add(type);
    }
}
