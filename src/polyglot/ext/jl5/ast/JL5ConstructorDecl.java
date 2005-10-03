package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5ConstructorDecl extends ConstructorDecl{

    public List paramTypes();
    public JL5ConstructorDecl paramTypes(List paramTypes);
}
