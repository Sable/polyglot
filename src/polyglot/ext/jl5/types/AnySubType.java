package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;

public interface AnySubType extends ReferenceType {

    Type bound();
    Type convertToInferred(List typeVars, List inferredTypes);
    boolean equivalentImpl(TypeObject arg2);
}
