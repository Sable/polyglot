package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface JL5Context extends Context {

    public VarInstance findVariableInThisScope(String name);
    public VarInstance findVariableSilent(String name);
}
