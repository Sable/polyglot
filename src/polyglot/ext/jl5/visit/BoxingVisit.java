package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;

public interface BoxingVisit {

    Node boxing(BoxingVisitor sv) throws SemanticException;
}
