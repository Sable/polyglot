package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5Catch_c extends Catch_c implements JL5Catch {

    public JL5Catch_c(Position pos, Formal formal, Block body){
        super(pos, formal, body);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException{
        Type t = formal.type().type();
        if (t instanceof ParameterizedType){
            throw new SemanticException("Cannot have a parameterized type for a catch formal", formal.position());
        }
        else if (t instanceof IntersectionType){
            throw new SemanticException("Cannot have a type variable for a catch formal", formal.position());
        }
        return super.typeCheck(tc);
    }
}
