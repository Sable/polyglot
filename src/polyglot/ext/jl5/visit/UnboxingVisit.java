package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;

public interface UnboxingVisit {

    Node unboxing(UnboxingVisitor sv) throws SemanticException;
}
