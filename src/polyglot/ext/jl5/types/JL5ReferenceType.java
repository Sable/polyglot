package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface JL5ReferenceType extends ReferenceType {
    
    EnumInstance enumConstantNamed(String name);
}
