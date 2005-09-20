package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5GenericConstructorDecl extends JL5ConstructorDecl {

    public List paramTypes();
    public JL5GenericConstructorDecl paramTypes(List paramTypes);
}
