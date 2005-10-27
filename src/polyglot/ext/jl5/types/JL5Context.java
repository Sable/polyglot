package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;
public interface JL5Context extends Context {

    public VarInstance findVariableInThisScope(String name);
    public VarInstance findVariableSilent(String name);

    public JL5Context pushTypeVariable(IntersectionType iType);
    public IntersectionType findTypeVariableInThisScope(String name);

    public boolean inTypeVariable();


    public JL5Context addTypeVariable(IntersectionType type);

    public MethodInstance findGenericMethod(String name, List argTypes, List inferredTypes) throws SemanticException;
}
