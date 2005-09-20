package polyglot.ext.jl5.types;

import java.util.*;

import polyglot.ast.*;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

public class GenericMethodInstance_c extends JL5MethodInstance_c implements GenericMethodInstance {

    protected List typeVariables;
   
    public GenericMethodInstance_c(TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type returnType, String name, List formals, List excTypes, List typeVariables){
        super(ts, pos, container, flags, returnType, name, formals, excTypes);
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
