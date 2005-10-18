package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;

public interface JL5MethodInstance extends MethodInstance{

    public boolean isCompilerGenerated();
    public JL5MethodInstance setCompilerGenerated(boolean val);

    List typeVariables();
    void addTypeVariable(IntersectionType type);

    boolean hasTypeVariable(String name);
    IntersectionType getTypeVariable(String name);

    void typeVariables(List vars);
    boolean isGeneric();
}
