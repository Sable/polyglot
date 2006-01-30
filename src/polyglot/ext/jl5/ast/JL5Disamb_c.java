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
import polyglot.ext.jl.types.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;

public class JL5Disamb_c extends Disamb_c implements JL5Disamb {

        
    protected Node disambiguateTypeNodePrefix(TypeNode tn) throws SemanticException {
        Type t = tn.type();
    
        if (t.isReference() && exprOK()){
            try {
                FieldInstance fi = ((JL5TypeSystem)ts).findFieldOrEnum(t.toReference(), name, c.currentClass());
                return ((JL5NodeFactory)nf).JL5Field(pos, tn, name).fieldInstance(fi);
            }
            catch(NoMemberException e){
                if (e.getKind() != e.FIELD && e.getKind() != JL5NoMemberException.ENUM_CONSTANT){
                    throw e;
                }
            }
        }

        if (t.isClass() && typeOK()){
            Resolver tc = ts.classContextResolver(t.toClass());
            Named n = tc.find(name);
            if (n instanceof Type){
                Type type = (Type)n;
                return nf.CanonicalTypeNode(pos, type);
            }
        }
        return null;
    }

    protected Node disambiguateExprPrefix(Expr e) throws SemanticException {
        if (exprOK()){
            return ((JL5NodeFactory)nf).JL5Field(pos, e, name);        
        }
        return null;
    }


    protected Node disambiguateVarInstance(VarInstance vi) throws SemanticException {
        if (vi instanceof FieldInstance) {
            FieldInstance fi = (FieldInstance) vi;
            Receiver r = makeMissingFieldTarget(fi);
            return ((JL5NodeFactory)nf).JL5Field(pos, r, name).fieldInstance(fi).targetImplicit(true);
        } else if (vi instanceof LocalInstance) {
            LocalInstance li = (LocalInstance) vi;
            return nf.Local(pos, name).localInstance(li);
        }
        return null;
    }


    protected Node disambiguateNoPrefix() throws SemanticException {
       
        // First try local variables and fields
        VarInstance vi = c.findVariableSilent(name);
        
        if (vi != null && exprOK()){
            Node n = disambiguateVarInstance(vi);
            if (n != null) return n;
        }

        //if (((JL5Context)c).inTypeVariable()){
        IntersectionType res = ((JL5Context)c).findTypeVariableInThisScope(name);
        if (res != null){
            return nf.CanonicalTypeNode(pos, res);
        }
        //}
        
        // no variable found. try
        // might be a generic type parameter
        /*JL5ParsedClassType ct = (JL5ParsedClassType)c.currentClass();
    
        
        if ((ct != null ) && ct.isGeneric()){
            if (ct.hasTypeVariable(name)){
          /*      IntersectionType it =  ct.getTypeVariable(name);
                return nf.CanonicalTypeNode(pos, it);
            }
        }

        // may be a generic type param for method or constr
        // header 
        if (c.inCode()){
            CodeInstance ci = c.currentCode();
            if (ci instanceof JL5MethodInstance && ((JL5MethodInstance)ci).isGeneric()){
                if (((JL5MethodInstance)ci).hasTypeVariable(name)){
                    return nf.CanonicalTypeNode(pos, ((JL5MethodInstance)ci).getTypeVariable(name));
                }
            }
            else if (ci instanceof JL5ConstructorInstance && ((JL5ConstructorInstance)ci).isGeneric()){
                if (((JL5ConstructorInstance)ci).hasTypeVariable(name)){
                    return nf.CanonicalTypeNode(pos, ((JL5ConstructorInstance)ci).getTypeVariable(name));
                }
            }
        }*/
        
        if (typeOK()) {
            try {
                Named n = c.find(name);
                if (n instanceof Type) {
                    Type type = (Type) n;
                    return nf.CanonicalTypeNode(pos, type);
                }
            } catch (NoClassException e) {
                if (!name.equals(e.getClassName())) {
                    // hmm, something else must have gone wrong
                    // rethrow the exception
                    throw e;
                }

                // couldn't find a type named name. 
                // It must be a package--ignore the exception.
            }
        }


        // Must be a package then...
        if (packageOK()) {
            return nf.PackageNode(pos, ts.packageForName(name));
        }

        return null;
 
        
        //Node result = null;
        //if (result == null){
            // make special AmbNoPrefix node and return it (it should 
            // extend Expr)
            // later have a pass (after type checking) to deal with these
            // nodes which may be an enum constant of the type of a 
            // switch expr (this situation may arise for case labels)
            // otherwise as far as I know its an error
                
          //  result = ((JL5NodeFactory)nf).JL5AmbExpr(pos, name);
        //}
        //return result;
    }
}
