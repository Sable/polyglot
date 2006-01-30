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

public class AnyType_c extends ReferenceType_c implements AnyType, SignatureType{

    protected Type upperBound;
    
    public AnyType_c(TypeSystem ts){
        super(ts, null);
    }

    public String translate(Resolver c){
        return "?";
    }

    public String toString(){
        return "?";
    }

    public Type upperBound(){
        if (upperBound == null) return typeSystem().Object();
        return upperBound;
    }
    
    public void upperBound(Type t){
        upperBound = t;
    }
    
    public boolean hasMethodImpl(MethodInstance mi){
        return upperBound().toReference().hasMethodImpl(mi);
    }

    public List methodsNamed(String name){
        return upperBound().toReference().methodsNamed(name);
    }

    public FieldInstance fieldNamed(String name){
        return upperBound().toReference().fieldNamed(name);
    }

    public List methods(String name, List args){
        return upperBound().toReference().methods(name, args);
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

    public boolean descendsFromImpl(Type ancestor){
        return true; // ? is descends from everything
    }

    public String signature(){
        return "*";
    }
}
