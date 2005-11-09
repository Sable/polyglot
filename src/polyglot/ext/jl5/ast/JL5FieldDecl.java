package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;
import polyglot.ext.jl5.visit.*;
import polyglot.types.*;

public interface JL5FieldDecl extends FieldDecl{

    public boolean isCompilerGenerated();
    public JL5FieldDecl setCompilerGenerated(boolean val);

    public List runtimeAnnotations();
    public List classAnnotations();
    public List sourceAnnotations();

    Node simplify(SimplifyVisitor sv) throws SemanticException;
}
