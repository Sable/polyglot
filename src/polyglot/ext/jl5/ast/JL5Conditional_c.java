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

public class JL5Conditional_c extends Conditional_c implements JL5Conditional, UnboxingVisit, BoxingVisit {

    public JL5Conditional_c(Position pos, Expr cond, Expr consequent, Expr alternative){
        super(pos, cond, consequent, alternative);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        if (!ts.equals(cond.type(), ts.Boolean()) && !ts.equals(cond.type(), ts.BooleanWrapper())){
            throw new SemanticException("Condition of ternary expression must be of type boolean.", cond.position());
        }

        Expr e1 = consequent;
        Expr e2 = alternative;
        Type t1 = e1.type();
        Type t2 = e2.type();
        
        // From the JLS, section:
        // If the second and third operands have the same type (which may be
        // the null type), then that is the type of the conditional expression.
        if (ts.equals(t1, t2) || ts.equivalent(t1, t2)) {
            return type(t1);
        }

        // Otherwise, if the second and third operands have numeric type, then
        // there are several cases:
        if (t1.isNumeric() && t2.isNumeric()) {
            // - If one of the operands is of type byte and the other is of
            // type short, then the type of the conditional expression is
            // short.
            if (t1.isByte() && t2.isShort() || t1.isShort() && t2.isByte()) {
                return type(ts.Short());
            }

            // - If one of the operands is of type T where T is byte, short, or
            // char, and the other operand is a constant expression of type int
            // whose value is representable in type T, then the type of the
            // conditional expression is T.

            if (t1.isIntOrLess() &&
                t2.isInt() &&
                ts.numericConversionValid(t1, e2.constantValue())) {
                    return type(t1);
            }

            if (t2.isIntOrLess() &&
                t1.isInt() &&
                ts.numericConversionValid(t2, e1.constantValue())) {
                    return type(t2);
            }

            // - Otherwise, binary numeric promotion (§5.6.2) is applied to the
            // operand types, and the type of the conditional expression is the
            // promoted type of the second and third operands. Note that binary
            // numeric promotion performs value set conversion (§5.1.8).
            return type(ts.promote(t1, t2));
        }

        // If one of the second and third operands is of the null type and the
        // type of the other is a reference type, then the type of the
        // conditional expression is that reference type.
        if (t1.isNull() && t2.isReference()) return type(t2);
        if (t2.isNull() && t1.isReference()) return type(t1);


        // if one is null and other is primitive return 
        // type will need rewriting later
        if (t1.isNull() && t2.isPrimitive()) return type(((JL5TypeSystem)ts).classOf(t2));
        if (t2.isNull() && t1.isPrimitive()) return type(((JL5TypeSystem)ts).classOf(t1));
        
        // If the second and third operands are of different reference types,
        // then it must be possible to convert one of the types to the other
        // type (call this latter type T) by assignment conversion (§5.2); the
        // type of the conditional expression is T. It is a compile-time error
        // if neither type is assignment compatible with the other type.

        if (t1.isReference() && t2.isReference()) {
            if (ts.isImplicitCastValid(t1, t2)) {
                return type(t2);
            }
            if (ts.isImplicitCastValid(t2, t1)) {
                return type(t1);
            }
        }

        throw new SemanticException(
            "Could not find a type for ternary conditional expression.",
            position());
    }

    public Node unboxing(UnboxingVisitor v) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();
        Conditional node = this;
        if (node.cond().type().isClass()){
            node = (Conditional)node.cond(nf.createUnboxed(node.cond().position(), node.cond(), ts.Boolean(), ts, v.context()));
        }
        if (ts.needsUnboxing(node.type(), node.consequent().type())){
            node = (Conditional)node.consequent(nf.createUnboxed(node.consequent().position(), node.consequent(), node.type(), ts, v.context()));
        }
        if (ts.needsUnboxing(node.type(), node.alternative().type())){
            node = (Conditional)node.alternative(nf.createUnboxed(node.alternative().position(), node.alternative(), node.type(), ts, v.context()));
        }
        return node; 
    }

    public Node boxing(BoxingVisitor v) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();
        Conditional node = this;
        if (ts.needsBoxing(node.type(), node.consequent().type())){
            node = (Conditional)node.consequent(nf.createBoxed(node.consequent().position(), node.consequent(), node.type(), ts, v.context()));
        }
        if (ts.needsBoxing(node.type(), node.alternative().type())){
            node = (Conditional)node.alternative(nf.createBoxed(node.alternative().position(), node.alternative(), node.type(), ts, v.context()));
        }
        return node; 
    }

}
