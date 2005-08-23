package polyglot.ext.jl5.ast;

import polyglot.ast.*;

public interface JL5MethodDecl extends MethodDecl {

    public boolean isCompilerGenerated();
    public JL5MethodDecl setCompilerGenerated(boolean val);
        
}
