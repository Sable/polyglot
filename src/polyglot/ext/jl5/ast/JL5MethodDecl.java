package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5MethodDecl extends MethodDecl {

    public boolean isCompilerGenerated();
    public JL5MethodDecl setCompilerGenerated(boolean val);
   
    public List paramTypes();
    public JL5MethodDecl paramTypes(List paramTypes);
         
    public List runtimeAnnotations();
    public List classAnnotations();
    public List sourceAnnotations();
}
