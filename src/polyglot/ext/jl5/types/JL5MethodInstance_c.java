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

    /*public JL5MethodInstance_c(TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type returnType, String name, List formals, List excTypes, List typeVariables){
        super(ts, pos, container, flags, returnType, name, formals, excTypes);
        this.typeVariables = typeVariables;
    }*/
    
    
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

    public void typeVariables(List vars){
        typeVariables = vars;
    }
    
    public boolean isGeneric(){
        if ((typeVariables != null) && !typeVariables.isEmpty()) return true;
        return false;
    }

    public boolean callValidImpl(List argTypes){
        List l1 = this.formalTypes();
        List l2 = argTypes;

        for (int i = 0; i < l1.size(); i++){
            Type t1 = (Type)l1.get(i);
            if (l2.size() > i){
                Type t2 = (Type)l2.get(i);
            

                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    if (ts.isImplicitCastValid(t2, t1)){
                        return true;
                    }
                    for (int j = i; j < l2.size(); j++){
                        Type tv = (Type)l2.get(j);
                        if (!ts.isImplicitCastValid(tv, ((ArrayType)t1).base())){
                            return false;
                        }
                    }
                }
                else {
                    if (!ts.isImplicitCastValid(t2, t1)) {
                        return false;
                    }
                }
            }
            else {
                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return true; 
    }
}
