package polyglot.ext.jl5.types;

import polyglot.types.*;

public interface JL5MethodInstance extends MethodInstance{

    public boolean isCompilerGenerated();
    public JL5MethodInstance setCompilerGenerated(boolean val);
}
