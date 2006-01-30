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
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import java.util.*;

public class JL5LocalAssign_c extends LocalAssign_c implements JL5LocalAssign, BoxingVisit, UnboxingVisit, LetInsertionVisit {

    public JL5LocalAssign_c(Position pos, Local left, Operator op, Expr right){
        super(pos, left, op, right);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        if (right() instanceof Call && ((JL5MethodInstance)((Call)right()).methodInstance()).isGeneric()){
            if (!ts.isImplicitCastValid(right.type(), left.type()) && 
                !ts.equals(right.type(), left.type())){
                // determine infered type of type vars in init type
                // and update init type
                List formalTypes = new ArrayList();
                formalTypes.add(right.type());
                List argTypes = new ArrayList();
                argTypes.add(left.type());
                List inferredTypes = ts.inferTypesFromArgs(((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables(), formalTypes, argTypes, new ArrayList());
                Type inferred = right().type();
                if (right().type() instanceof IntersectionType && ((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables().contains(right().type())){
                    int pos = ((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables().indexOf(right().type());
                    inferred = (Type)inferredTypes.get(pos);
                }
                else if (right().type() instanceof ParameterizedType){
                    inferred = ((ParameterizedType)right().type()).convertToInferred(((JL5MethodInstance)((Call)right()).methodInstance()).typeVariables(), inferredTypes);
                }
                if (inferred != right().type()){
                    return right(((JL5Call)right()).type(inferred)).typeCheck(tc);
                }
            }
        }
    
        return super.typeCheck(tc);
    }
    
    public Node boxing(BoxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        JL5LocalAssign node = this;
        /*if (ts.needsBoxing(node.type(), node.left().type())){
            node = (JL5LocalAssign)node.left(nf.createBoxed(node.left().position(), node.left(), ts, sv.context()));
        }*/
        if (ts.needsBoxing(node.type(), node.right().type())){
            node = (JL5LocalAssign)node.right(nf.createBoxed(node.right().position(), node.right(), node.type(), ts, sv.context()));
        }
        
        return node;
                
    }
    
    public Node unboxing(UnboxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();

        JL5LocalAssign node = this;
        if (ts.needsUnboxing(node.type(), node.right().type())){
            node = (JL5LocalAssign)node.right(nf.createUnboxed(node.right().position(), node.right(), node.type(), ts, sv.context()));
        }
        return node;
                
    }
    
    public Node insertLet(LetInsertionVisitor v) throws SemanticException{
        // don't do anything for regular assigns
        if (operator() == Assign.ASSIGN) return this;
        
        // only care about op assigns
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();

        FlagAnnotations fl = new FlagAnnotations();
        fl.classicFlags(Flags.NONE);
        fl.annotations(new ArrayList());

        Local e = (Local)left();

        JL5LocalDecl ld = nf.JL5LocalDecl(e.position(), fl, nf.CanonicalTypeNode(e.position(), e.type()), "$arg", e);
        ld = (JL5LocalDecl)ld.localInstance(ts.localInstance(e.position(), Flags.NONE, e.type(), "$arg"));

        Local local = nf.Local(e.position(), "$arg");
        local = (Local)local.localInstance(ld.localInstance());
        local = (Local)local.type(e.type());

        Expr binary = (JL5Binary)nf.JL5Binary(e.position(), local, nf.getBinOpFromAssignOp(operator()), right()).type(e.type());

        Assign assign = (Assign)nf.JL5LocalAssign(e.position(), e, Assign.ASSIGN, binary).type(e.type());

        return nf.JL5Let(e.position(), ld, assign).type(e.type());
    }

}
