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

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.frontend.*;

public class JL5ConstructorCall_c extends ConstructorCall_c implements JL5ConstructorCall {

    protected List typeArguments;

    public JL5ConstructorCall_c(Position pos, Kind kind, Expr qualifier, List arguments, List typeArguments){
        super(pos, kind, qualifier, arguments);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5ConstructorCall typeArguments(List args){
        JL5ConstructorCall_c n = (JL5ConstructorCall_c) copy();
        n.typeArguments = args;
        return n;
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5ConstructorCall_c n = (JL5ConstructorCall_c)super.typeCheck(tc);
        return checkTypeArguments(tc, n);
    }
    
    private Node checkTypeArguments(TypeChecker tc, JL5ConstructorCall_c n) throws SemanticException {
       
        // can only call a method with type args if it was declared as generic
        if (!typeArguments.isEmpty() && !((JL5ConstructorInstance)n.constructorInstance()).isGeneric()) {
            throw new SemanticException("Cannot invoke instructor "+this+" with type arguments", position());
        }
       
        if (!typeArguments().isEmpty() && typeArguments.size() != ((JL5ConstructorInstance)n.constructorInstance()).typeVariables().size()){
            throw new SemanticException("Cannot invoke instructor "+this+" with wrong number of type arguments", position());
        }
        
        JL5ConstructorInstance ci = (JL5ConstructorInstance)n.constructorInstance();
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        
        // wildcards are not allowed for type args for generic new
        for (int i = 0; i < typeArguments.size(); i++) {
            TypeNode correspondingArg = (TypeNode)typeArguments.get(i);
            if (correspondingArg instanceof BoundedTypeNode){
                throw new SemanticException("Wilcard argument not allowed here", correspondingArg.position());
            }
        }

        if (!typeArguments.isEmpty()) {
            for (int i = 0; i < typeArguments().size(); i++){
                Type arg = ((TypeNode)typeArguments().get(i)).type();
                Type decl = (Type)ci.typeVariables().get(i);

                for (int j = 0; j < ci.formalTypes().size(); j++){
                    Type formal = (Type)ci.formalTypes().get(j);
                    Type argType = ((Expr)arguments().get(j)).type();

                    if (ts.equals(formal, decl)){
                        if (!ts.isImplicitCastValid(argType, arg)){
                            throw new SemanticException("Found arg of type: "+argType+" expected: "+arg, ((Expr)arguments().get(j)).position());
                        }
                    }
                }
            }
        }
        
        // type check arguments
        if (qualifier() != null && qualifier().type() instanceof ParameterizedType){
            for (int i = 0; i < ci.formalTypes().size(); i++){
                Type t = (Type)ci.formalTypes().get(i);
                if (t instanceof IntersectionType){
                    Type other = ts.findRequiredType((IntersectionType)t, (ParameterizedType)qualifier().type());
                    if (!ts.isImplicitCastValid(((Expr)arguments().get(i)).type(), other)){
                        throw new SemanticException("Found arg of type: "+((Expr)arguments().get(i)).type()+" expected: "+other, ((Expr)arguments().get(i)).position());
                    }
                }
            }
        
        }
        return n;
    }
}
