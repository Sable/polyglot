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
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.visit.*;
import polyglot.ext.jl5.types.*;

public class JL5CanonicalTypeNode_c extends CanonicalTypeNode_c implements JL5CanonicalTypeNode{

    public JL5CanonicalTypeNode_c(Position pos, Type type) {
        super(pos, type);
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (type() instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType)type();
            if (pt.baseType() instanceof IntersectionType){
                throw new SemanticException("Unexpected type: only class types can have type arguments.", position());
            }
            if (!pt.typeArguments().isEmpty() && pt.typeArguments().size() != pt.typeVariables().size()){
                    throw new SemanticException("Must give type arguments for all declared type variables or none.", position());
            }
            for (int i = 0; i < pt.typeVariables().size(); i++){
                Type arg = (Type)pt.typeArguments().get(i);
                if (!tc.typeSystem().isSubtype(arg, (Type)pt.typeVariables().get(i))){
                    throw new SemanticException("Invalid type argument "+arg, position());
                }
            }
        }
        return super.typeCheck(tc);
    }

}
