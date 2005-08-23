package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.ext.jl.types.*;
import polyglot.util.*;
import java.util.*;

public class JL5MethodInstance_c extends MethodInstance_c implements JL5MethodInstance{

    public JL5MethodInstance_c(TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type returnType, String name, List formals, List excTypes){
        super(ts, pos, container, flags, returnType, name, formals, excTypes);
    }
    
    protected boolean compilerGenerated;
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
}
