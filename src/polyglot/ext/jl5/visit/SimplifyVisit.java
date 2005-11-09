package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;

public interface SimplifyVisit {

    Node simplify(SimplifyVisitor sv) throws SemanticException;
}
