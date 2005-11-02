package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface AnyType extends ReferenceType {

    Type upperBound();
    void upperBound(Type t);
}
