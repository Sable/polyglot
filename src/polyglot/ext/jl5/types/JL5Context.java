package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface JL5Context extends Context {

    public VarInstance findVariableInThisScope(String name);
    public VarInstance findVariableSilent(String name);

    public JL5Context pushTypeVariable(IntersectionType iType);
    public IntersectionType findTypeVariableInThisScope(String name);

    public boolean inTypeVariable();

    public JL5Context addTypeVariable(IntersectionType type);

}
