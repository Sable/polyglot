package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5Case_c extends Case_c implements JL5Case  {


    public JL5Case_c(Position pos, Expr expr){
        super(pos, expr);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
    
        if (expr == null) {
	        return this;
	    }

        TypeSystem ts = tc.typeSystem();

        if (! ts.isImplicitCastValid(expr.type(), ts.Int())){
            if (expr.type() instanceof ClassType && !JL5Flags.isEnumModifier(expr.type().toClass().flags())) {
                throw new SemanticException("Case label must be an byte, char, short, or int.", position());
            }
        }

        Object o = null;

        if (expr instanceof Field) {
            FieldInstance fi = ((Field) expr).fieldInstance();

            if (fi == null) {
                throw new InternalCompilerError(
                "Undefined FieldInstance after type-checking.");
            }

            if (! fi.isConstant()) {
                throw new SemanticException("Case label must be an integral constant.", position());
            }

            o = fi.constantValue();
        }
        else if (expr instanceof Local) {
            LocalInstance li = ((Local) expr).localInstance();

            if (li == null) {
                throw new InternalCompilerError(
                "Undefined LocalInstance after type-checking.");
            }

            if (! li.isConstant()) {
                    /* FIXME: isConstant() is incorrect 
                throw new SemanticException("Case label must be an integral constant.",
                            position());
                    */
                    return this;
            }

            o = li.constantValue();
        }
        else {
            o = expr.constantValue();
        }
        if (o instanceof Number && ! (o instanceof Long) &&
            ! (o instanceof Float) && ! (o instanceof Double)) {

            return value(((Number) o).longValue());
        }
        else if (o instanceof Character) {
            return value(((Character) o).charValue());
        }

        throw new SemanticException("Case label must be an integral constant.",
                                    position());

    }
}
