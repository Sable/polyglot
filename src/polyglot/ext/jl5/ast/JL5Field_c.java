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
import polyglot.util.*;
import polyglot.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.types.*;
import polyglot.visit.*;

public class JL5Field_c extends Field_c implements JL5Field {

    public JL5Field_c (Position pos, Receiver target, String name){
        super(pos, target, name);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Context c = tc.context();
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();

        if (! target.type().isReference()){
            throw new SemanticException("Cannot access field \"" + name +
             "\" " + (target instanceof Expr
             ? "on an expression "
             : "") +
             "of non-reference type \"" +
             target.type() + "\".", target.position());
        }

        FieldInstance fi = ts.findFieldOrEnum(target.type().toReference(), name, c.currentClass());

        if (fi == null) {
            throw new InternalCompilerError("Cannot access field on node of type "+ target.getClass().getName() + ".");
        }

        JL5Field_c f = (JL5Field_c)fieldInstance(fi).type(fi.type());
        f.checkConsistency(c);
        
        if (target() != null && target().type() instanceof ParameterizedType &&  fi.type() instanceof IntersectionType){
            Type other = ts.findRequiredType((IntersectionType)fi.type(), (ParameterizedType)target().type());
            return f.type(other);
        }
       
        if (target() != null && target().type() instanceof ParameterizedType &&  fi.type() instanceof ParameterizedType){
            if (ts.equals(((ParameterizedType)fi.type()).baseType(), ((ParameterizedType)target.type()).baseType())){
                return f.type((ParameterizedType)target().type());
            }
        }
        
        if (target() != null && target().type() instanceof ClassType && ((ClassType)target().type()).isAnonymous() && fi.type() instanceof IntersectionType){
            Type other = ts.findRequiredType((IntersectionType)fi.type(), (ParameterizedType)((ClassType)target().type()).superType());
            return f.type(other);    
        }
      
        if (target() != null && target().type() instanceof JL5ParsedClassType){
            if (((JL5ParsedClassType)target().type()).typeVariables() != null){
                if (!((JL5ParsedClassType)target().type()).typeVariables().isEmpty() && fi.type() instanceof IntersectionType){
                    // strictly from raw type other is erasure
                    Type other = ((IntersectionType)fi.type()).erasureType();
                    return f.type(other);
                }
            }
        }
        return f;
        
    }

    public boolean isConstant(){
        if (JL5Flags.isEnumModifier(flags())) return true;
        if (fieldInstance() instanceof EnumInstance) return true;
        return super.isConstant();
    }
    public void checkConsistency(Context c){
        
        //super.checkConsistency(c);
        //this consistency checking has problems when dealing with gen
        //types
    }
}
