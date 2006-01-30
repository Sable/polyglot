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

package polyglot.ext.jl5.types.reflect;

import polyglot.types.reflect.*;
import java.io.*;
import polyglot.ext.jl5.types.*;
import polyglot.types.*;
import java.util.*;
public class JL5Method extends Method{

    protected boolean defaultVal;
    protected Signature signature;
    
    public JL5Method(DataInputStream in, ClassFile clazz) {
        super(in, clazz);
    }

    public void initialize() throws IOException {
        modifiers = in.readUnsignedShort();

        name = in.readUnsignedShort();
        type = in.readUnsignedShort();
                
        int numAttributes = in.readUnsignedShort();
                     
        attrs = new Attribute[numAttributes];
        for (int i = 0; i < numAttributes; i++) {
            int nameIndex = in.readUnsignedShort();
            int length = in.readInt();
            Constant name = clazz.constants()[nameIndex];

            if (name != null){
                if ("Exceptions".equals(name.value())) {
                    exceptions = new Exceptions(clazz, in, nameIndex, length);
                    attrs[i] = exceptions;
                }
                if ("Synthetic".equals(name.value())) {
                    synthetic = true;
                }
                if ("AnnotationDefault".equals(name.value())){
                    defaultVal = true;
                }
                
                if("Signature".equals(name.value())){
                    signature = new Signature(in, nameIndex, length, clazz);
                    attrs[i] = signature;
                }
            }

            if (attrs[i] == null){
                long n = in.skip(length);
                if (n != length){
                    throw new EOFException();
                }
            }
        }
                                                  
    }
    
    public boolean hasDefaultVal(){
        return defaultVal;
    }

    public MethodInstance methodInstance(TypeSystem ts, ClassType ct){
        JL5MethodInstance mi = (JL5MethodInstance)super.methodInstance(ts, ct);
        if (signature != null){
            try {
                signature.parseMethodSignature(ts, mi.position(), ct);
            }
            catch(IOException e){
            }
            catch(SemanticException e){
            }

            mi.typeVariables(signature.methodSignature.typeVars());
            mi = (JL5MethodInstance)mi.returnType(signature.methodSignature.returnType());
            mi = (JL5MethodInstance)mi.formalTypes(signature.methodSignature.formalTypes());
            mi = (JL5MethodInstance)mi.throwTypes(signature.methodSignature.throwTypes());
        }
        if (mi.flags().isTransient()){
            ArrayList newFormals = new ArrayList();
            for (Iterator it = mi.formalTypes().iterator(); it.hasNext(); ){
                Type t = (Type)it.next();
                if (!it.hasNext()){
                    ArrayType at = ((JL5TypeSystem)ts).arrayType(t.position(), ((ArrayType)t).base());
                    ((JL5ArrayType)at).setVariable();
                    newFormals.add(at);
                } 
                else{
                    newFormals.add(t);
                }
            }
            mi = (JL5MethodInstance)mi.formalTypes(newFormals);
        }
        return mi;
    }

    public ConstructorInstance constructorInstance(TypeSystem ts, ClassType ct, Field[] fields){
        JL5ConstructorInstance ci = (JL5ConstructorInstance)super.constructorInstance(ts, ct, fields);
        if (signature != null){
            try {
                signature.parseMethodSignature(ts, ci.position(), ct);
            }
            catch(IOException e){
            }
            catch(SemanticException e){
            }

            ci.typeVariables(signature.methodSignature.typeVars());
            ci = (JL5ConstructorInstance)ci.formalTypes(signature.methodSignature.formalTypes());
            ci = (JL5ConstructorInstance)ci.throwTypes(signature.methodSignature.throwTypes());
    
        }
        if (ci.flags().isTransient()){
            ArrayList newFormals = new ArrayList();
            for (Iterator it = ci.formalTypes().iterator(); it.hasNext(); ){
                Type t = (Type)it.next();
                if (!it.hasNext()){
                    ArrayType at = ((JL5TypeSystem)ts).arrayType(t.position(), ((ArrayType)t).base());
                    ((JL5ArrayType)at).setVariable();
                    newFormals.add(at);
                } 
                else{
                    newFormals.add(t);
                }
            }
            ci = (JL5ConstructorInstance)ci.formalTypes(newFormals);
        }
        return ci;
    }
}

