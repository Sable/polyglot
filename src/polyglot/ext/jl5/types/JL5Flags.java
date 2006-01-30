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

import polyglot.types.*;
import polyglot.ext.jl5.ast.*;
import java.util.*;
import polyglot.util.*;

public class JL5Flags extends Flags {
   
    /*static final int[] print_order = new int[64];
    static int next_bit = 0;
    static final String [] flag_names = new String[64];
    long bits;*/

    public static final int ANNOTATION_MOD = 8192;
    public static final int ENUM_MOD = 16384;

    public static final Flags ENUM = createFlag("enum", null);
    public static final Flags ANNOTATION = createFlag("annotation", null);
    /*public static final Flags NONE         = new JL5Flags(0);
    public static final Flags PUBLIC       = createFlag("public", null);
    public static final Flags PRIVATE      = createFlag("private", null);
    public static final Flags PROTECTED    = createFlag("protected", null);
    public static final Flags STATIC       = createFlag("static", null);
    public static final Flags FINAL        = createFlag("final", null);
    public static final Flags SYNCHRONIZED = createFlag("synchronized", null);
    public static final Flags TRANSIENT    = createFlag("transient", null);
    public static final Flags NATIVE       = createFlag("native", null);
    public static final Flags INTERFACE    = createFlag("interface", null);
    public static final Flags ABSTRACT     = createFlag("abstract", null);
    public static final Flags VOLATILE     = createFlag("volatile", null);
    public static final Flags STRICTFP     = createFlag("strictfp", null);
    
    protected static final Flags ACCESS_FLAGS = PUBLIC.set(PRIVATE).set(PROTECTED);
    protected List annotationElems;
    */
    public JL5Flags(){
        super(0L);
    }
    

    /*public static Flags createFlag(String name, JL5Flags after){
        if (next_bit >= flag_names.length) 
            throw new InternalCompilerError("too many flags");
        if (print_order[next_bit] != 0)    
            throw new InternalCompilerError("print_order and next_bit " +
                                            "inconsistent");
        if (flag_names[next_bit] != null)
            throw new InternalCompilerError("flag_names and next_bit " +
                                            "inconsistent");

        int bit = next_bit++;
        flag_names[bit] = name;

        if (after == null) {
            print_order[bit] = bit;
        }
        else {
            for (int i = bit; i > 0; i--) {
                if ((after.bits & print_order[i]) != 0)
                    break;

                // shift up and fill in the gap with f
                print_order[i] = print_order[i-1];
                print_order[i-1] = bit;
            }
        }

        return new JL5Flags(1L << bit);
  
    }
   
    public Flags set(JL5Flags other){
        JL5Flags n = new JL5Flags(bits | other.bits);
        n.annotationElems(annotationElems());
        return n;
    }

    public Flags clear(JL5Flags other){
        JL5Flags n = new JL5Flags(bits & ~other.bits);
        n.annotationElems(annotationElems());
        return n;
    }

    public Flags retain(JL5Flags other){
        JL5Flags n = new JL5Flags(bits & other.bits);
        n.annotationElems(annotationElems());
        return n;
    }*/
 
    public static Flags setEnumModifier(Flags f){
        return f.set(ENUM);
    }

    public static Flags clearEnumModifier(Flags f){
        return f.clear(ENUM);
    }

    public static boolean isEnumModifier(Flags f){
        return f.contains(ENUM);
    }
    
    public static Flags setAnnotationModifier(Flags f){
        return f.set(ANNOTATION);
    }

    public static Flags clearAnnotationModifier(Flags f){
        return f.clear(ANNOTATION);
    }

    public static boolean isAnnotationModifier(Flags f){
        return f.contains(ANNOTATION);
    }

    /*public JL5Flags addAnnotationElem(AnnotationElem elem){
        if (annotationElems == null){
            annotationElems = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        annotationElems.add(elem);
        return this;
    }

    public List annotationElems(){
        return annotationElems;
    }

    public void annotationElems(List elems){
        annotationElems = elems;
    }*/
    
}
