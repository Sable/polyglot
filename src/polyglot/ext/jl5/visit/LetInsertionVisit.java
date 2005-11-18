package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;

public interface LetInsertionVisit {

    Node insertLet(LetInsertionVisitor sv) throws SemanticException;
}
