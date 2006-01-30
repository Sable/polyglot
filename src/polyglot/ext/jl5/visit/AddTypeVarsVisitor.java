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

package polyglot.ext.jl5.visit;

import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.types.Package;
import polyglot.main.Report;

import java.io.IOException;
import java.util.*;
import polyglot.visit.*;


/** Visitor which traverses the AST constructing type objects. */
public class AddTypeVarsVisitor extends ContextVisitor
{
    public AddTypeVarsVisitor(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    protected NodeVisitor enterCall(Node n) throws SemanticException {
        if (n instanceof TypeVarsAdder){
            return ((TypeVarsAdder)n).addTypeVars(this);
        }
        return this;
    }

    /*protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
      if (Report.should_report(Report.visit, 4))
	Report.report(4, "<< AddMemberVisitor::leave " + n);
        return n.del().addMembers((AddMemberVisitor) v);
    }*/
}
