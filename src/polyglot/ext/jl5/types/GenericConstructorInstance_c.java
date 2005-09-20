package polyglot.ext.jl5.types;

import java.util.*;

import polyglot.ast.*;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ext.jl.types.*;

public class GenericConstructorInstance_c extends ConstructorInstance_c implements GenericConstructorInstance {

    protected List typeVariables;
   
    public GenericConstructorInstance_c(TypeSystem ts, Position pos, ClassType container, Flags flags, List formals, List excTypes, List typeVariables){
        super(ts, pos, container, flags, formals, excTypes);
        this.typeVariables = typeVariables;
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
    
    public boolean hasTypeVariable(String name){
        for (Iterator it = typeVariables.iterator(); it.hasNext(); ){
            IntersectionType iType = (IntersectionType)it.next();
            if (iType.name().equals(name)) return true;
        }
        return false;
    }
    
    public IntersectionType getTypeVariable(String name){
        for (Iterator it = typeVariables.iterator(); it.hasNext(); ){
            IntersectionType iType = (IntersectionType)it.next();
            if (iType.name().equals(name)) return iType;
        }
        return null;
    }

}
