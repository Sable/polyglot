package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;

public interface GenericMethodInstance extends JL5MethodInstance{

    List typeVariables();
    void addTypeVariable(IntersectionType type);

    boolean hasTypeVariable(String name);
    IntersectionType getTypeVariable(String name);
}
