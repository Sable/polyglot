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
import polyglot.main.Options;

public class JL5Assert_c extends Assert_c implements JL5Assert, UnboxingVisit {

    public JL5Assert_c(Position pos, Expr cond, Expr errorMsg){
        super(pos, cond, errorMsg);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        
        if (! Options.global.assertions) {
            ErrorQueue eq = tc.errorQueue();
            eq.enqueue(ErrorInfo.WARNING,
                       "assert statements are disabled. Recompile " +
                       "with -assert and ensure the post compiler supports " +
                       "assert (e.g., -post \"javac -source 1.4\"). " +
                       "Removing the statement and continuing.",
                       cond.position());
        }
        
        if (!ts.equals(cond.type(), ts.Boolean()) && !ts.equals(cond.type(), ts.BooleanWrapper())){
            throw new SemanticException("Condition of assert statement must have boolean type.", cond.position());
        }
        
        if (errorMessage != null && ts.equals(errorMessage.type(), ts.Void())) {
            throw new SemanticException("Error message in assert statement " +
                                        "must have a value.",
                                        errorMessage.position());
        }

        return this;
    }
    
    public Node unboxing(UnboxingVisitor v) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)v.typeSystem();
        JL5NodeFactory nf = (JL5NodeFactory)v.nodeFactory();

        if (cond().type().isClass()){
            return cond(nf.createUnboxed(cond().position(), cond(), ts.Boolean(), ts, v.context()));
        }
        return this;
    }
}
