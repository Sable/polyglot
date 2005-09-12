package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;

public interface ApplicationCheck {

    Node applicationCheck(ApplicationChecker ac) throws SemanticException;
}
