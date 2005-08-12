package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;

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
}
