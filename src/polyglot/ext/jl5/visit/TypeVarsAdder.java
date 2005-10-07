package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;

public interface TypeVarsAdder{

    NodeVisitor addTypeVars(AddTypeVarsVisitor av) throws SemanticException;
}
