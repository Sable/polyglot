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

public class AnySuperType_c extends ReferenceType_c implements AnySuperType, SignatureType{

    protected Type bound;
    protected Type upperBound;
    
    public AnySuperType_c(TypeSystem ts, Type bound){
        super(ts);
        this.bound = bound;
    }

    public Type bound(){
        return bound;
    }
    
    public Type upperBound(){
        if (upperBound == null) return typeSystem().Object();
        return upperBound;
    }

    public void upperBound(Type t){
        upperBound = t;
    }
    
    public String translate(Resolver c){
        return "? super "+bound.translate(c);
    }

    public String toString(){
        return "? super "+bound.toString();
    }
    
    public boolean hasMethodImpl(MethodInstance mi){
        return upperBound().toReference().hasMethodImpl(mi);
    }

    public List methods(String name, List args){
        return upperBound().toReference().methods(name, args);
    }

    public List methodsNamed(String name){
        return upperBound().toReference().methodsNamed(name);
    }

    public FieldInstance fieldNamed(String name){
        return upperBound().toReference().fieldNamed(name);
    }

    public List methods(){
        return upperBound().toReference().methods();
    }

    public List fields(){
        return upperBound().toReference().fields();
    }

    public List interfaces(){
        return upperBound().toReference().interfaces();
    }

    public Type superType(){
        return upperBound().toReference().superType();
    }

    public ReferenceType toReference(){
        return this;
    }
    
    public boolean isReference(){
        return true;
    }

    public boolean isClass(){
        return true;
    }
    
    public boolean equivalentImpl(TypeObject ancestor){
        if (ancestor instanceof AnySuperType){
            return ts.equals(bound(), ((AnySuperType)ancestor).bound());
        }
        return false;
    }

    public boolean descendsFromImpl(Type ancestor){
        if (ancestor instanceof JL5ParsedClassType || ancestor instanceof ParameterizedType){
            return typeSystem().isSubtype(upperBound(), ancestor);
        }
        else if (ancestor instanceof AnySuperType){
            return typeSystem().isSubtype(((AnySuperType)ancestor).bound(), bound());
        }
        else if (ancestor instanceof AnySubType){
            return typeSystem().isSubtype(upperBound(), ((AnySubType)ancestor).bound());
        }
        else if (ancestor instanceof AnyType){
            // this one will be true if base types have subtype relation ??
            return typeSystem().isSubtype(upperBound(), ((AnyType)ancestor).upperBound());
        }
        else if (ancestor instanceof IntersectionType){
            return typeSystem().isSubtype(bound(), ancestor);
        }
        return false;
    }

    public String signature(){
        return "-"+((SignatureType)bound).signature();
    }
}
