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
import polyglot.types.*;
import java.util.*;

public class AnySubType_c extends ReferenceType_c implements AnySubType, SignatureType{

    protected Type bound;
    
    public AnySubType_c(TypeSystem ts, Type bound){
        super(ts);
        this.bound = bound;
    }
    
    public Type bound(){
        return bound;
    }

    public String translate(Resolver c){
        return "? extends "+bound.translate(c);
    }

    public String toString(){
        return "? extends "+bound.toString();
    }

    public boolean hasMethodImpl(MethodInstance mi){
        return bound.toReference().hasMethodImpl(mi);
    }

    public List methods(String name, List args){
        return bound.toReference().methods(name, args);
    }

    public List methodsNamed(String name){
        return bound.toReference().methodsNamed(name);
    }

    public FieldInstance fieldNamed(String name){
        return bound.toReference().fieldNamed(name);
    }

    public List methods(){
        return bound.toReference().methods();
    }

    public List fields(){
        return bound.toReference().fields();
    }

    public List interfaces(){
        return bound.toReference().interfaces();
    }

    public Type superType(){
        return bound.toReference().superType();
    }

    public ReferenceType toReference(){
        return this;
    }
    
    public boolean isReference(){
        return true;
    }

    public Type convertToInferred(List typeVars, List inferredTypes){
        if (bound instanceof IntersectionType){
            return ((JL5TypeSystem)typeSystem()).anySubType((Type)inferredTypes.get(typeVars.indexOf(bound)));
        } 
        else if (bound instanceof ParameterizedType){
            return ((JL5TypeSystem)typeSystem()).anySubType(((ParameterizedType)bound).convertToInferred(typeVars, inferredTypes));
        }
        else {
            return this;
        }
    }

    public boolean equivalentImpl(TypeObject ancestor){
        if (ancestor instanceof AnySubType){
            return ts.equals(bound(), ((AnySubType)ancestor).bound());
        }
        return false;
    }

    public boolean descendsFromImpl(Type ancestor){
        if (ancestor instanceof JL5ParsedClassType || ancestor instanceof ParameterizedType){
            return ts.isSubtype(bound(), ancestor);
        }
        else if (ancestor instanceof AnySuperType){
            return ts.isSubtype(bound(), ((AnySuperType)ancestor).bound());
        }
        else if (ancestor instanceof AnySubType){
            return ts.isSubtype(bound(), ((AnySubType)ancestor).bound());
        }
        else if (ancestor instanceof AnyType){
            return ts.isSubtype(bound(), ((AnyType)ancestor).upperBound());
        }
        else if (ancestor instanceof IntersectionType){
            return ts.isSubtype(bound(), ancestor);
        }
        return false;
    }

    public String signature(){
        return "+"+((SignatureType)bound).signature();
    }
}
