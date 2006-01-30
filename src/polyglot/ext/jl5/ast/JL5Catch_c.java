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
