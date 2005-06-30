package polyglot.ext.jl5.types;

public interface LazyClassInitializer extends polyglot.types.LazyClassInitializer{

    public void initEnumConstants(ParsedClassType ct);
}
