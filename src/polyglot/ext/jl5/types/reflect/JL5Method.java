package polyglot.ext.jl5.types.reflect;

import polyglot.types.reflect.*;
import java.io.*;

public class JL5Method extends Method{

    boolean defaultVal;
    
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

}

