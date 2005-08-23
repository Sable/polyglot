package polyglot.ext.jl5.ast;

import polyglot.ast.*;

public interface ElementValuePair extends Expr{
    public String name();
    public Expr value();
}
