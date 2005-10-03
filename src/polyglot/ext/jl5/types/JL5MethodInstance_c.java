package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.ext.jl.types.*;
import polyglot.util.*;
import java.util.*;

public class JL5MethodInstance_c extends MethodInstance_c implements JL5MethodInstance{
    
    protected boolean compilerGenerated;
    protected List typeVariables;
    
    public JL5MethodInstance_c(TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type returnType, String name, List formals, List excTypes){
        super(ts, pos, container, flags, returnType, name, formals, excTypes);
    }

    public JL5MethodInstance_c(TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type returnType, String name, List formals, List excTypes, List typeVariables){
        super(ts, pos, container, flags, returnType, name, formals, excTypes);
        this.typeVariables = typeVariables;
    }
    
    
    public boolean isCompilerGenerated(){
        return compilerGenerated;
    }
        
    public JL5MethodInstance setCompilerGenerated(boolean val){
        if (compilerGenerated != val) {
            JL5MethodInstance_c mi = (JL5MethodInstance_c)copy();
            mi.compilerGenerated = val;
        
            return mi;
        }
        return this;
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

    public boolean isGeneric(){
        if ((typeVariables != null) && !typeVariables.isEmpty()) return true;
        return false;
    }
}
