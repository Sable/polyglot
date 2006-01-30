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
import polyglot.ext.jl.types.*;

public class JL5PrimitiveType_c extends PrimitiveType_c implements JL5PrimitiveType, SignatureType {
   
    public JL5PrimitiveType_c(TypeSystem ts, Kind kind){
        super(ts, kind);
    }
    
    public boolean isImplicitCastValidImpl(Type toType) {
        if (toType.isClass()){
            return isAutoBoxingValid(toType);
        }
        return super.isImplicitCastValidImpl(toType);
    }

    private boolean isAutoBoxingValid(Type toType){
        if (!toType.isClass()) return false;

        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        
        if (this.isInt() && (toType.isInt() || toType.isShort() || toType.isByte() || toType.isChar() || ts.isSubtype(ts.IntegerWrapper(), toType))) return true;
        if (this.isBoolean() && (toType.isBoolean() || ts.isSubtype(ts.BooleanWrapper(), toType)) ) return true;
        if (this.isByte() && (toType.isByte() || toType.isShort() || toType.isChar() || ts.isSubtype(ts.ByteWrapper(), toType))) return true;
        if (this.isShort() && (toType.isShort() || toType.isByte() || toType.isChar() || ts.isSubtype(ts.ShortWrapper(), toType) )) return true;
        if (this.isChar() && (toType.isShort() || toType.isByte() || toType.isChar() || ts.isSubtype(ts.CharacterWrapper(), toType))) return true;
        if (this.isLong() && (toType.isLong() || ts.isSubtype(ts.LongWrapper(), toType))) return true;
        if (this.isDouble() && (toType.isDouble() || ts.isSubtype(ts.DoubleWrapper(), toType))) return true;
        if (this.isFloat() && (toType.isFloat() || ts.isSubtype(ts.FloatWrapper(), toType))) return true;
        return false;
    }

    /*public boolean equalsImpl(TypeObject t) {
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (t.equals(ts.BooleanWrapper()) && this.isBoolean()) return true;
        if (t.equals(ts.IntegerWrapper()) && this.isInt()) return true;
        if (t.equals(ts.ByteWrapper()) && this.isByte()) return true;
        if (t.equals(ts.ShortWrapper()) && this.isShort()) return true;
        if (t.equals(ts.CharacterWrapper()) && this.isChar()) return true;
        if (t.equals(ts.LongWrapper()) && this.isLong()) return true;
        if (t.equals(ts.FloatWrapper()) && this.isFloat()) return true;
        if (t.equals(ts.DoubleWrapper()) && this.isDouble()) return true;
        return super.equalsImpl(t); 
    }*/

    public boolean equivalentImpl(TypeObject t) {
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (t.equals(ts.BooleanWrapper()) && this.isBoolean()) return true;
        if (t.equals(ts.IntegerWrapper()) && this.isInt()) return true;
        if (t.equals(ts.ByteWrapper()) && this.isByte()) return true;
        if (t.equals(ts.ShortWrapper()) && this.isShort()) return true;
        if (t.equals(ts.CharacterWrapper()) && this.isChar()) return true;
        if (t.equals(ts.LongWrapper()) && this.isLong()) return true;
        if (t.equals(ts.FloatWrapper()) && this.isFloat()) return true;
        if (t.equals(ts.DoubleWrapper()) && this.isDouble()) return true;
        return false; 
    }

    public boolean isCastValidImpl(Type toType){
        if (isVoid() || toType.isVoid()) return false;
        if (ts.equals(this, toType) || ((JL5TypeSystem)ts).equivalent(this, toType)) return true;
        if (toType.isClass()){
            /*if (this.isShort() && (toType.isInt() || toType.isLong() || toType.isFloat() || toType.isDouble())) return true;
            if (this.isInt() && (toType.isLong() || toType.isFloat() || toType.isDouble())) return true;
            if (this.isLong() && (toType.isFloat() || toType.isDouble())) return true;
            if (this.isFloat() && toType.isDouble()) return true;*/
            return false;
        }
        else if(isNumeric() && toType.isNumeric()){
            return true;    
        }
        return false;
    }

   /* public Type toWrappedClass(){
        if (isInt()) return ts.IntegerWrapper();
        if (isByte()) return ts.ByteWrapper();
        if (isBoolean()) return ts.BooleanWrapper();
    }*/

    public String signature(){
        if (this.isInt()) return "I";
        if (this.isByte()) return "B";
        if (this.isShort()) return "S";
        if (this.isChar()) return "C";
        if (this.isBoolean()) return "Z";
        if (this.isLong()) return "J";
        if (this.isDouble()) return "D";
        if (this.isFloat()) return "F";
        if (this.isVoid()) return "V";
        throw new RuntimeException("Unknown primitive type: "+this);
    }
}
