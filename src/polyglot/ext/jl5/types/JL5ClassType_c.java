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

package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.types.*;
import polyglot.ast.*;

public abstract class JL5ClassType_c extends ClassType_c implements JL5ClassType {

    public abstract List enumConstants();
    
    public EnumInstance enumConstantNamed(String name){
        for(Iterator it = enumConstants().iterator(); it.hasNext();){
            EnumInstance ei = (EnumInstance)it.next();
            if (ei.name().equals(name)){
                return ei;
            }
        }
        return null;
    }

    public boolean isImplicitCastValidImpl(Type toType){
        if (isAutoUnboxingValid(toType)) return true;
        if (toType instanceof IntersectionType){
            return isClassToIntersectionValid(toType);
        }
        return super.isImplicitCastValidImpl(toType);
    }

    private boolean isClassToIntersectionValid(Type toType){
        IntersectionType it = (IntersectionType)toType;
        if (it.bounds() == null || it.bounds().isEmpty()) return true;
        return ts.isImplicitCastValid(this, ((TypeNode)it.bounds().get(0)).type());
    }
    
    private boolean isAutoUnboxingValid(Type toType){
        
        if (!toType.isPrimitive()) return false;
        if (toType.isInt() && this.fullName().equals("java.lang.Integer")) return true;
        if (toType.isBoolean() && this.fullName().equals("java.lang.Boolean")) return true;
        if (toType.isByte() && this.fullName().equals("java.lang.Byte")) return true;
        if (toType.isShort() && this.fullName().equals("java.lang.Short")) return true;
        if (toType.isChar() && this.fullName().equals("java.lang.Character")) return true;
        if (toType.isLong() && this.fullName().equals("java.lang.Long")) return true;
        if (toType.isDouble() && this.fullName().equals("java.lang.Double")) return true;
        if (toType.isFloat() && this.fullName().equals("java.lang.Float")) return true;
        return false;
    }
}
