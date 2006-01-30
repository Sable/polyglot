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

public class JL5Instanceof_c extends Instanceof_c implements JL5Instanceof {

    public JL5Instanceof_c(Position pos, Expr expr, TypeNode compareType){
        super(pos, expr, compareType);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (compareType().type() instanceof ParameterizedType){
            throw new SemanticException("Type arguments not allowed here.", compareType().position());
        }
        if (compareType().type() instanceof IntersectionType){
            throw new SemanticException("Type variable not allowed here.", compareType().position());
        }
        return super.typeCheck(tc);
    }
}
