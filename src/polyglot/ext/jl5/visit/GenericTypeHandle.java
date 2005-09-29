package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;

public interface GenericTypeHandle {

    Node handleGenericType(GenericTypeHandler ac) throws SemanticException;
}
