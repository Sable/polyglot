package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;
import polyglot.ext.jl5.visit.*;
import polyglot.types.*;

public interface JL5ConstructorDecl extends ConstructorDecl{

    public boolean isCompilerGenerated();
    public JL5ConstructorDecl setCompilerGenerated(boolean val);

        
    public List paramTypes();
    public JL5ConstructorDecl paramTypes(List paramTypes);
    
    public List annotations();
    public List runtimeAnnotations();
    public List classAnnotations();
    public List sourceAnnotations();

    Node simplify(SimplifyVisitor sv) throws SemanticException;
}
