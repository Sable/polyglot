package polyglot.ext.jl5.types;

import java.util.*;

public interface GenericParsedClassType extends JL5ParsedClassType {

    List typeVariables();
    void addTypeVariable(IntersectionType type);
}
