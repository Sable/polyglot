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
import polyglot.ext.jl5.visit.*;

public class JL5Cast_c extends Cast_c implements JL5Cast, BoxingVisit, UnboxingVisit {

    public JL5Cast_c(Position pos, TypeNode castType, Expr expr){
        super(pos, castType, expr);
    }

    public Node boxing(BoxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        if (ts.needsBoxing(this.castType().type(), expr.type())){
            return expr(nf.createBoxed(expr.position(), expr, castType().type(), ts, sv.context()));
        }
        return this;
                
    }
    
    public Node unboxing(UnboxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        if (ts.needsUnboxing(this.castType().type(), expr.type())){
            return expr(nf.createUnboxed(expr.position(), expr, castType().type(), ts, sv.context()));
        }
        return this;
                
    }

    // the original of this method makes all numerics be doubles
    // which is bad
    public Type childExpectedType(Expr child, AscriptionVisitor av){
        TypeSystem ts = av.typeSystem();
    
        return child.type();
        /*if (child == expr){
            if (castType.type().isReference()){
                return ts.Object();
            }
        }*/
    }
}
