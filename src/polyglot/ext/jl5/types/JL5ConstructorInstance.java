package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;

public interface JL5ConstructorInstance extends ConstructorInstance{

    List typeVariables();
    void addTypeVariable(IntersectionType type);

    boolean hasTypeVariable(String name);
    IntersectionType getTypeVariable(String name);

    boolean isGeneric();
}
