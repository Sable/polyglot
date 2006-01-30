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

import polyglot.visit.*;
import polyglot.frontend.*;
import polyglot.types.*;
import polyglot.ast.*;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;

public class JL5TypeBuilder extends TypeBuilder {

    public JL5TypeBuilder(Job job, TypeSystem ts, NodeFactory nf){
        super(job, ts, nf);
    }

    /*public TypeBuilder pushGenericClass(Position pos, Flags flags, String name) throws SemanticException {
        GenericParsedClassType t = newGenericClass(pos, flags, name);
        return pushClass(t);
    }

    protected GenericParsedClassType newGenericClass(Position pos, Flags flags, String name) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        GenericParsedClassType ct = ts.createGenericClassType(this.job.source());
        ct.flags(flags);
        ct.name(name);
        ct.position(pos);
        
        if (currentPackage() != null){
            ct.package_(currentPackage());
        }

        // FIXME: This requires code duplication (from poorly formed 
        // super class)
        if (inCode) {
            ct.kind(ClassType.LOCAL);
            ct.outer(currentClass());
            return ct;
        }
        else if (currentClass() != null){
            ct.kind(ClassType.MEMBER);
            ct.outer(currentClass());
            currentClass().addMemberClass(ct);

            ClassType container = ct.outer();
            boolean allMembers = (container.isMember() || container.isTopLevel());
            while (container.isMember()){
                container = container.outer();
                allMembers = allMembers && (container.isMember() || container.isTopLevel());
            }
            
            if (allMembers){
                typeSystem().parsedResolver().addNamed( typeSystem().getTransformedClassName(ct), ct);
            }
            
            return ct;
        }
        else {
            ct.kind(ClassType.TOP_LEVEL);
            Named dup = ((CachingResolver) typeSystem().systemResolver()).check(ct.fullName());
            
            if (dup != null && dup.fullName().equals(ct.fullName())){
                throw new SemanticException("Duplicate class \"" + ct.fullName() + "\".", pos);
            }
            typeSystem().parsedResolver().addNamed(ct.fullName(), ct);
            ((CachingResolver)typeSystem().systemResolver()).addNamed(ct.fullName(), ct);
            return ct;
        }
        
    }*/
}
