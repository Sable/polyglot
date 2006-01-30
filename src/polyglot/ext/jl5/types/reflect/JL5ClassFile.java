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
import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.ext.jl5.types.*;
import java.io.*;
import polyglot.util.*;

public class JL5ClassFile extends ClassFile implements JL5LazyClassInitializer {

    protected Signature signature;
    
    public JL5ClassFile(File classFileSource, byte[] code, ExtensionInfo ext){
        super(classFileSource, code, ext);
    }

    public void initEnumConstants(JL5ParsedClassType ct){
        JL5TypeSystem ts = (JL5TypeSystem)ct.typeSystem();
        
        for (int i = 0; i < fields.length; i++){
            if ((fields[i].modifiers() & JL5Flags.ENUM_MOD) != 0) {
                FieldInstance fi = fields[i].fieldInstance(ts, ct);
                ct.addEnumConstant(ts.enumInstance(ct.position(), ct, fi.flags(), fi.name(), ct));
            }
        }
    }

    public void initAnnotations(JL5ParsedClassType ct){
        JL5TypeSystem ts = (JL5TypeSystem)ct.typeSystem();

        for (int i = 0; i < methods.length; i++){
            MethodInstance mi = methods[i].methodInstance(ts, ct);
            AnnotationElemInstance ai = ts.annotationElemInstance(ct.position(), ct, mi.flags(), mi.returnType(), mi.name(), ((JL5Method)methods[i]).hasDefaultVal());
            ct.addAnnotationElem(ai);
        }
    }
    
  
    public ParsedClassType type(TypeSystem ts) throws SemanticException {
    
        JL5ParsedClassType t = (JL5ParsedClassType)super.type(ts);
        if (signature != null){
            try {
                signature.parseClassSignature(ts, t.position());
            }
            catch(IOException e){
            }

            t.typeVariables(signature.classSignature.typeVars());
        }
        return t;
    }
    
    public void readAttributes(DataInputStream in) throws IOException {
        int numAttributes = in.readUnsignedShort();
        attrs = new Attribute[numAttributes];
    
        for (int i = 0; i < numAttributes; i++){
            int nameIndex = in.readUnsignedShort();
            int length = in.readInt();
            if ("InnerClasses".equals(constants[nameIndex].value())) {
                innerClasses = new InnerClasses(in, nameIndex, length);
                attrs[i] = innerClasses;
            }
            else if ("Signature".equals(constants[nameIndex].value())){
                signature = new Signature(in, nameIndex, length, this);
                attrs[i] = signature;
            }
            else {
                long n = in.skip(length);
                if (n != length) {
                    throw new EOFException();
                }
            }
        }
    }


}
