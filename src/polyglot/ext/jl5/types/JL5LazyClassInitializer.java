package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface JL5LazyClassInitializer extends LazyClassInitializer{

    public void initEnumConstants(JL5ParsedClassType ct);
}
