package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;

public interface JL5Case extends Case {

    public Case_c reconstruct(Expr expr);
}
