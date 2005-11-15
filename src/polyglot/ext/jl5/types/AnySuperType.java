package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface AnySuperType extends ReferenceType {

    Type bound();
    Type upperBound();
    void upperBound(Type t);

    boolean equivalentImpl(TypeObject arg2);
}
