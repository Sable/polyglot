package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;

public class JL5ClassLit_c extends ClassLit_c implements JL5ClassLit{

    public JL5ClassLit_c(Position pos, TypeNode typeNode){
        super(pos, typeNode);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (typeNode().type() instanceof IntersectionType){
            throw new SemanticException("Type variable not allowed here.", typeNode.position());
        }
        return this;
    }
}
