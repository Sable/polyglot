package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5GenericMethodDecl extends JL5MethodDecl {

    public List paramTypes();
    public JL5GenericMethodDecl paramTypes(List paramTypes);
}
