package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import java.util.*;

public class JL5Context_c extends Context_c implements JL5Context {

    public JL5Context_c(TypeSystem ts){
        super(ts);
    }
    
    public VarInstance findVariableInThisScope(String name){
        
        VarInstance vi = null;
        //try{
        vi = super.findVariableInThisScope(name);
        //}
        //catch(NoMemberException e){
            
        
        if (vi == null && isClass()){
            try {
                return ((JL5TypeSystem)typeSystem()).findEnumConstant(this.type, name, this.type);
            }
            catch (SemanticException e2){
                return null;
            }
        }
        return vi;
    }

    public VarInstance findVariableSilent(String name){
        VarInstance vi = findVariableInThisScope(name);
        if (vi != null){
            return vi;
        }
        if (outer != null){
            return outer.findVariableSilent(name);
        }
        return null;
    }

    public Named findInThisScope(String name){
        if (types != null){
            for (Iterator typesIt = types.keySet().iterator(); typesIt.hasNext();){
                String nextType = (String)typesIt.next();
                Named next = (Named)types.get(nextType);
                if (next instanceof JL5ParsedClassType && ((JL5ParsedClassType)next).isGeneric()){
                    JL5ParsedClassType ct = (JL5ParsedClassType)next;
                    if (ct.hasTypeVariable(name)){
                        return ct.getTypeVariable(name);
                    }
                }
            }
        }
        return super.findInThisScope(name);
    }
}
