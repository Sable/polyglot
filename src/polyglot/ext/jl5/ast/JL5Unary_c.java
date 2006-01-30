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
import java.util.*;

public class JL5Unary_c extends Unary_c implements JL5Unary, LetInsertionVisit, UnboxingVisit {

    public JL5Unary_c(Position pos, Unary.Operator op, Expr expr){
        super(pos, op, expr);
    }

    public Node insertLet(LetInsertionVisitor v) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();

        if (operator() == Unary.NEG || operator() == Unary.POS || operator() == Unary.BIT_NOT || operator() == Unary.NOT) {
            return this;
        }
        else {
            if (expr() instanceof Local) {
                return makeInner(expr(), operator(), nf, ts);
            }
            else if (expr() instanceof Field){
                Field field = (Field)expr();
                
                if (field.target() instanceof Expr){
                    LocalDecl ld = nf.JL5LocalDecl(field.target().position(), new FlagAnnotations(Flags.NONE, new ArrayList()), nf.CanonicalTypeNode(field.target().position(), field.target().type()), "$arg", (Expr)field.target()).localInstance(ts.localInstance(field.target().position(), Flags.NONE, field.target().type(), "$arg"));

                    Expr local = nf.Local(field.target().position(), "$arg").localInstance(ld.localInstance()).type(field.target().type());
                    
                    Expr baseField = nf.JL5Field(field.position(), local, field.name()).fieldInstance(field.fieldInstance()).type(field.type());

                    Expr newExpr = makeInner(baseField, operator(), nf, ts);

                    return nf.JL5Let(field.position(), ld, newExpr).type(field.type());
                }
                else {
                    return makeInner(field, operator(), nf, ts);
                }
            }
            else if (expr() instanceof ArrayAccess){
                FlagAnnotations fl = new FlagAnnotations();
                fl.classicFlags(Flags.NONE);
                fl.annotations(new ArrayList());
                
                ArrayAccess aa = (ArrayAccess)expr();
                LocalDecl ld = nf.JL5LocalDecl(aa.array().position(), new FlagAnnotations(Flags.NONE, new ArrayList()), nf.CanonicalTypeNode(aa.array().position(), aa.array().type()), "$arg", aa.array()).localInstance(ts.localInstance(aa.array().position(), Flags.NONE, aa.array().type(), "$arg"));
                
                Expr baseLocal = nf.Local(aa.array().position(), "$arg").localInstance(ld.localInstance()).type(aa.array().type());
                
                    LocalDecl ldindex = nf.JL5LocalDecl(aa.index().position(), new FlagAnnotations(Flags.NONE, new ArrayList()), nf.CanonicalTypeNode(aa.index().position(), aa.index().type()), "$arg2", aa.array()).localInstance(ts.localInstance(aa.index().position(), Flags.NONE, aa.index().type(), "$arg2"));
                    
                    Expr indexLocal = nf.Local(aa.index().position(), "$arg2").localInstance(ldindex.localInstance()).type(aa.index().type());
                    
                    Expr baseAccess = nf.JL5ArrayAccess(aa.position(), baseLocal, indexLocal).type(aa.type());

                    Expr newExpr = makeInner(baseAccess, operator(), nf, ts);

                    JL5Let inner = (JL5Let)nf.JL5Let(aa.index().position(), ldindex, newExpr).type(aa.type());
               return nf.JL5Let(aa.position(), ld, inner).type(aa.type());
            }
        }
          
        return this;
    }

    private Expr makeInner(Expr e, Unary.Operator op, JL5NodeFactory nf, JL5TypeSystem ts){
        if (operator() == Unary.PRE_INC || operator() == Unary.PRE_DEC){
            return makeInnerPre(e, op, nf, ts);
        }
        else if (operator() == Unary.POST_INC || operator() == Unary.POST_DEC){
            return makeInnerPost(e, op, nf, ts);
        }
        throw new RuntimeException("Unknown unary operator: "+op); 
    }
    private Expr makeInnerPost(Expr e, Unary.Operator op, JL5NodeFactory nf, JL5TypeSystem ts){

        LocalDecl ld = nf.JL5LocalDecl(e.position(), new FlagAnnotations(Flags.NONE, new ArrayList()), nf.CanonicalTypeNode(e.position(), e.type()), "$arg", e).localInstance(ts.localInstance(e.position(), Flags.NONE, e.type(), "$arg"));

        Expr local = nf.Local(e.position(), "$arg").localInstance(ld.localInstance()).type(e.type());
        
        JL5Binary binary = (JL5Binary)nf.JL5Binary(e.position(), local, op == Unary.POST_INC ? Binary.ADD : Binary.SUB, makeLit(e, nf, ts)).type(e.type());
        
        Assign assign = null;
        if (e instanceof Local){    
            assign = (Assign)nf.JL5LocalAssign(e.position(), e, Assign.ASSIGN, binary).type(e.type());
        }
        else if (e instanceof Field){
            assign = (Assign)nf.JL5FieldAssign(e.position(), e, Assign.ASSIGN, binary).type(e.type());
        }
        else {
            assign = (Assign)nf.JL5ArrayAccessAssign(e.position(), e, Assign.ASSIGN, binary).type(e.type());
        }
        
        LocalDecl outerld = nf.JL5LocalDecl(e.position(), new FlagAnnotations(Flags.NONE, new ArrayList()), nf.CanonicalTypeNode(e.position(), e.type()), "$arg2", assign).localInstance(ts.localInstance(e.position(), Flags.NONE, e.type(), "$arg2"));

        JL5Let inner = (JL5Let)nf.JL5Let(e.position(), outerld, local).type(e.type());
        return nf.JL5Let(e.position(), ld, inner).type(e.type());
        
    }

    private Expr makeInnerPre(Expr e, Unary.Operator op, JL5NodeFactory nf, JL5TypeSystem ts){

        LocalDecl ld = nf.JL5LocalDecl(e.position(), new FlagAnnotations(Flags.NONE, new ArrayList()), nf.CanonicalTypeNode(e.position(), e.type()), "$arg", e).localInstance(ts.localInstance(e.position(), Flags.NONE, e.type(), "$arg"));

        Expr local = nf.Local(e.position(), "$arg").localInstance(ld.localInstance()).type(e.type());
        
        JL5Binary binary = (JL5Binary)nf.JL5Binary(e.position(), local, op == Unary.PRE_INC ? Binary.ADD : Binary.SUB, makeLit(e, nf, ts)).type(e.type());
        
        Assign assign = null;
        if (e instanceof Local){    
            assign = (Assign)nf.JL5LocalAssign(e.position(), e, Assign.ASSIGN, binary).type(e.type());
        }
        else if (e instanceof Field){
            assign = (Assign)nf.JL5FieldAssign(e.position(), e, Assign.ASSIGN, binary).type(e.type());
        }
        else {
            assign = (Assign)nf.JL5ArrayAccessAssign(e.position(), e, Assign.ASSIGN, binary).type(e.type());
        }

        return nf.JL5Let(e.position(), ld, assign).type(e.type());
    }

    private Expr makeLit(Expr e, JL5NodeFactory nf, JL5TypeSystem ts){
        if (e.type().isFloat()) return nf.FloatLit(e.position(), FloatLit.FLOAT, 1F).type(ts.Float());
        if (e.type().isDouble()) return nf.FloatLit(e.position(), FloatLit.DOUBLE, 1.0).type(ts.Double());
        if (e.type().isLong()) return nf.IntLit(e.position(), IntLit.LONG, 1L).type(ts.Long());
        return nf.IntLit(e.position(), IntLit.INT, 1).type(ts.Int());
    }
    
   
    // for +x, -x, ~x and !x x must be unboxed (if necessary) to a 
    // primitive
    public Node unboxing(UnboxingVisitor sv) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)sv.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)sv.nodeFactory();
        if (expr.type().isClass()){
            Expr n = (Expr)nf.createUnboxed(expr.position(), expr(), ts.primitiveOf(expr.type()), ts, sv.context());
            return expr(n).type(n.type());
        }
        return this;
                
    }
}
