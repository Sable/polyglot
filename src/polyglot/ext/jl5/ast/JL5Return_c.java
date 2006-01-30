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

import polyglot.ext.jl.ast.*;
import polyglot.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5Return_c extends Return_c implements JL5Return, UnboxingVisit, BoxingVisit {

    public JL5Return_c(Position pos, Expr expr){
        super(pos, expr);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av){
        if (child == expr){
            Context c = av.context();
            if (c.currentCode() instanceof MethodInstance){
                return ((MethodInstance)c.currentCode()).returnType();
            }
        }
        return child.type();
    }

    public Node unboxing(UnboxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        Context c = bv.context();
        
        if (c.currentCode() instanceof MethodInstance && expr != null){
            if (ts.needsUnboxing(((MethodInstance)c.currentCode()).returnType(), expr.type())){
                return expr(nf.createUnboxed(expr().position(), expr(), ((MethodInstance)c.currentCode()).returnType(), ts, c));
            }
        }        
        return this;
    }
    
    public Node boxing(BoxingVisitor bv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)bv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)bv.nodeFactory();
        Context c = bv.context();
        
        if (c.currentCode() instanceof MethodInstance && expr != null){
            if (ts.needsBoxing(((MethodInstance)c.currentCode()).returnType(), expr.type())){
                return expr(nf.createBoxed(expr().position(), expr(), ((MethodInstance)c.currentCode()).returnType(), ts, c));
            }
        }        
        return this;
    }
}
