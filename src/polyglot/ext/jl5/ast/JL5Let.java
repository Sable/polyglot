package polyglot.ext.jl5.ast;

import polyglot.ast.*;

public interface JL5Let extends Expr {

    LocalDecl localDecl();
    //Expr alpha();
    Expr beta();
    JL5Let beta(Expr e);
    //JL5Let alpha(Expr e);
}
