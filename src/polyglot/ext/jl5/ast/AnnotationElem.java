package polyglot.ext.jl5.ast;

import polyglot.ast.*;

public interface AnnotationElem extends Expr {

    TypeNode typeName();
    AnnotationElem typeName(TypeNode typeName);
}
