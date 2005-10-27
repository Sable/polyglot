package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.types.*;

public interface ParameterizedType extends JL5ParsedClassType {

    List typeArguments();
    void typeArguments(List args);

    JL5ParsedClassType baseType();

    boolean equalsImpl(TypeObject t);

    boolean comprisedOfIntersectionType(IntersectionType iType);
    Type convertToInferred(List typeVars, List inferredTypes);
}
