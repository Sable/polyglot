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
import polyglot.util.*;

public class JL5Context_c extends Context_c implements JL5Context {

    protected Map typeVars;
    protected IntersectionType typeVariable;

    public static final Kind TYPE_VAR = new Kind("type-var");
    
    public JL5Context_c(TypeSystem ts){
        super(ts);
    }
    
    public VarInstance findVariableInThisScope(String name){
        
        VarInstance vi = null;
        //try{
        vi = super.findVariableInThisScope(name);
        //}
        //catch(NoMemberException e){
            
        if (vi == null && isClass()){
            try {
                return ((JL5TypeSystem)typeSystem()).findEnumConstant(this.type, name, this.type);
            }
            catch (SemanticException e2){
                return null;
            }
        }
        return vi;
    }

    public VarInstance findVariableSilent(String name){
        VarInstance vi = findVariableInThisScope(name);
        if (vi != null){
            return vi;
        }

        try {
        // might be static
        if (importTable() != null){
            JL5ImportTable jit = (JL5ImportTable)importTable();
            for (Iterator it = jit.memberImports().iterator(); it.hasNext(); ){
                String next = (String)it.next();
                String id = StringUtil.getShortNameComponent(next);
                if (name.equals(id)){
                    Named nt = ts.forName(StringUtil.getPackageComponent(next));
                    if (nt instanceof Type){
                        Type t = (Type)nt;
                        try {
                            vi = ts.findField(t.toClass(), name);
                        }
                        catch(SemanticException e){}
                        if (vi != null){
                            return vi;
                        }
                    }
                }
            }
            if (vi == null){
                for (Iterator it = jit.staticClassImports().iterator(); it.hasNext(); ){
                    String next = (String)it.next();
                    Named nt = ts.forName(next);
                    if (nt instanceof Type){
                        Type t = (Type)nt;
                        try {
                            vi = ts.findField(t.toClass(), name);
                        }
                        catch(SemanticException e){
                        }
                        if (vi != null) return vi;
                    }
                }
            }
        }
        }
        catch(SemanticException e){
        }
        
        if (outer != null){
            return outer.findVariableSilent(name);
        }
        return null;
    }

    public Named findInThisScope(String name){
        if (types != null){
            for (Iterator typesIt = types.keySet().iterator(); typesIt.hasNext();){
                String nextType = (String)typesIt.next();
                Named next = (Named)types.get(nextType);
                if (next instanceof JL5ParsedClassType && ((JL5ParsedClassType)next).isGeneric()){
                    JL5ParsedClassType ct = (JL5ParsedClassType)next;
                    if (ct.hasTypeVariable(name)){
                        return ct.getTypeVariable(name);
                    }
                }
            }
        }
        return super.findInThisScope(name);
    }

    public MethodInstance findMethod(String name, List argTypes) throws SemanticException {
        if (this.currentClass() != null && ts.hasMethodNamed(this.currentClass(), name)){
            return ts.findMethod(this.currentClass(), name, argTypes, this.currentClass());
        }

        if (importTable() != null){
        JL5ImportTable jit = (JL5ImportTable)importTable();
        for (Iterator it = jit.memberImports().iterator(); it.hasNext(); ){
            String next = (String)it.next();
            String id = StringUtil.getShortNameComponent(next);
            if (name.equals(id)){
                Named nt = ts.forName(StringUtil.getPackageComponent(next));
                if (nt instanceof Type){
                    Type t = (Type)nt;
                    if (t.isClass() && ts.hasMethodNamed(t.toClass(), name)){
                        return ts.findMethod(t.toClass(), name, argTypes, this.currentClass());
                    }
                }
            }
        }

        for (Iterator it = jit.staticClassImports().iterator(); it.hasNext(); ){
            String next = (String)it.next();
            Named nt = ts.forName(next);
            if (nt instanceof Type){
                Type t = (Type)nt;
                if (t.isClass() && ts.hasMethodNamed(t.toClass(), name)){
                    return ts.findMethod(t.toClass(), name, argTypes, this.currentClass());
                }
            }
        }
        }
        if (outer != null){
            return outer.findMethod(name, argTypes);
        }

        throw new SemanticException("Method "+name+" not found.");
    }
   
    protected Context_c push(){
        Context_c v = super.push();//(Context_c) this.copy();
        //v.outer = this;
        //v.types = null;
        //v.vars = null;
        ((JL5Context_c)v).typeVars = null;
        return v;
    }
    
    public JL5Context pushTypeVariable(IntersectionType iType){
        JL5Context_c v = (JL5Context_c)push();
        v.typeVariable = iType;
        v.kind = TYPE_VAR;
        //v.outer = this;
        return v;
    }
    
    public IntersectionType findTypeVariableInThisScope(String name){
        if (typeVariable != null && typeVariable.name().equals(name)) return typeVariable;
        if (typeVars != null && typeVars.containsKey(name)){
            return (IntersectionType)typeVars.get(name);
        }
        if (outer != null){
            return ((JL5Context)outer).findTypeVariableInThisScope(name);
        }
        return null;
    }

    public boolean inTypeVariable(){
        return kind == TYPE_VAR;
    }

    public String toString(){
        return super.toString() + "type var: "+typeVariable;
    }

    public JL5Context addTypeVariable(IntersectionType type){
        if (typeVars == null) typeVars = new HashMap();
        typeVars.put(type.name(), type);
        return this;
    }
    
    public MethodInstance findGenericMethod(String name, List argTypes, List inferredTypes) throws SemanticException {
        if (this.currentClass() != null && typeSystem().hasMethodNamed(this.currentClass(), name)){
            return ((JL5TypeSystem)typeSystem()).findGenericMethod(this.currentClass(), name, argTypes, this.currentClass(), inferredTypes);
        }
        if (outer != null){
            return ((JL5Context)outer).findGenericMethod(name, argTypes, inferredTypes);
        }
        throw new SemanticException("Method "+name+" not found");
    }
    
}
