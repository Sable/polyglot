/* Copyright (C) 2006 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
