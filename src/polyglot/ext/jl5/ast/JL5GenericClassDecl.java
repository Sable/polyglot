package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;

public interface JL5GenericClassDecl extends JL5ClassDecl {

    public List paramTypes();
    public JL5GenericClassDecl paramTypes(List paramTypes);
}
