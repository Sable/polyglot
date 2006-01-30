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

public class JL5Binary_c extends Binary_c implements JL5Binary, UnboxingVisit {

    public JL5Binary_c(Position pos, Expr left, Binary.Operator op, Expr right){
        super(pos, left, op, right);
    }

    public Node unboxing(UnboxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        JL5Binary node = this;
        if (operator() == Binary.EQ || operator() == Binary.NE){
            if (node.left().type().isReference() && !(node.right().type().isNull() || node.right().type().isReference())){
                node = (JL5Binary)node.left(nf.createUnboxed(node.left().position(), node.left(), ts.primitiveOf(node.left().type()), ts, sv.context()));
                node = (JL5Binary)node.type(node.left().type());
            }
            if (node.right().type().isClass() && !(node.left().type().isNull() || node.left().type().isReference())){
                node = (JL5Binary)node.right(nf.createUnboxed(node.right().position(), node.right(), ts.primitiveOf(node.right().type()), ts, sv.context()));
                node = (JL5Binary)node.type(node.left().type());
            }
        }
        else {
            if (node.left().type().isClass()){
                node = (JL5Binary)node.left(nf.createUnboxed(node.left().position(), node.left(), ts.primitiveOf(node.left().type()), ts, sv.context()));
                node = (JL5Binary)node.type(node.left().type());
            }
            if (node.right().type().isClass()){
                node = (JL5Binary)node.right(nf.createUnboxed(node.right().position(), node.right(), ts.primitiveOf(node.right().type()), ts, sv.context()));
                node = (JL5Binary)node.type(node.left().type());
            }
        }
        return node;
                
    }
}
