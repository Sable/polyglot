package polyglot.ext.jl5.types;

import java.util.*;

import polyglot.ast.*;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ext.jl.types.*;

public class JL5ConstructorInstance_c extends ConstructorInstance_c implements JL5ConstructorInstance {

    protected List typeVariables;
   
    public JL5ConstructorInstance_c(TypeSystem ts, Position pos, ClassType container, Flags flags, List formals, List excTypes){
        super(ts, pos, container, flags, formals, excTypes);
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
