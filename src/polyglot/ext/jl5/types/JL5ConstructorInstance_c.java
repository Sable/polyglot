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

import java.util.*;

import polyglot.ast.*;
import polyglot.main.Report;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ext.jl.types.*;

public class JL5ConstructorInstance_c extends ConstructorInstance_c implements JL5ConstructorInstance {

    protected List typeVariables;
   
    public JL5ConstructorInstance_c(TypeSystem ts, Position pos, ClassType container, Flags flags, List formals, List excTypes){
        super(ts, pos, container, flags, formals, excTypes);
    }
    
    public List typeVariables(){
        return typeVariables;
    }
    
    public void addTypeVariable(IntersectionType type){
        if (typeVariables == null){
            typeVariables = new TypedList(new LinkedList(), IntersectionType.class, false);
        }
        typeVariables.add(type);
    }
    
    public boolean hasTypeVariable(String name){
        for (Iterator it = typeVariables.iterator(); it.hasNext(); ){
            IntersectionType iType = (IntersectionType)it.next();
            if (iType.name().equals(name)) return true;
        }
        return false;
    }
    
    public IntersectionType getTypeVariable(String name){
        for (Iterator it = typeVariables.iterator(); it.hasNext(); ){
            IntersectionType iType = (IntersectionType)it.next();
            if (iType.name().equals(name)) return iType;
        }
        return null;
    }

    public void typeVariables(List vars){
        typeVariables = vars;
    }
    
    public boolean isGeneric(){
        if ((typeVariables != null) && !typeVariables.isEmpty()) return true;
        return false;
    }
    
    public boolean callValidImpl(List argTypes){
        /*if (this.isGeneric()){
            try {
                List inferredTypes = ((JL5TypeSystem)typeSystem()).inferTypesFromArgs(typeVariables(), formalTypes(), argTypes, new ArrayList());
                return genericCallValidImpl(argTypes, inferredTypes);
            }
            catch(SemanticException e){
                return false;
            }
        }*/
        List l1 = this.formalTypes();
        List l2 = argTypes;

        int i = 0; 
        for (i = 0; i < l1.size(); i++){
            Type t1 = (Type)l1.get(i);

            if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                // go through rest of args
                for (int j = i; j < l2.size(); j++){
                    Type t2 = (Type)l2.get(j);
                    if (!ts.isImplicitCastValid(t2, t1) && !ts.isImplicitCastValid(t2, ((ArrayType)t1).base())){
                        return false;
                    }
                }
                return true;
            }
            else {
                if (l2.size() <= i) return false;
                Type t2 = (Type)l2.get(i);
                if (!ts.isImplicitCastValid(t2, t1)){
                    return false;
                }
            }
        }
        if (i < l2.size()){
            return false;
        }
        else {
            return true;
        }

        /*List l1 = this.formalTypes();
        List l2 = argTypes;

        for (int i = 0; i < l1.size(); i++){
            Type t1 = (Type)l1.get(i);
            if (l2.size() > i){
                Type t2 = (Type)l2.get(i);
            

                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    if (ts.isImplicitCastValid(t2, t1)){
                        return true;
                    }
                    for (int j = i; j < l2.size(); j++){
                        Type tv = (Type)l2.get(j);
                        if (!ts.isImplicitCastValid(tv, ((ArrayType)t1).base())){
                            return false;
                        }
                    }
                }
                else {
                    if (!ts.isImplicitCastValid(t2, t1)) {
                        return false;
                    }
                }
            }
            else {
                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return true; */
    }
}
