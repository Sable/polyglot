package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.ext.jl.types.*;

public class JL5PrimitiveType_c extends PrimitiveType_c implements JL5PrimitiveType {
   
    public JL5PrimitiveType_c(TypeSystem ts, Kind kind){
        super(ts, kind);
    }
    
    public boolean isImplicitCastValidImpl(Type toType) {
        if (isAutoBoxingValid(toType)) return true;
        return super.isImplicitCastValidImpl(toType);
    }

    private boolean isAutoBoxingValid(Type toType){
        if (!toType.isClass()) return false;

        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        
        if (this.isInt() && (((ClassType)toType).fullName().equals("java.lang.Integer") || ts.IntegerWrapper().isSubtype(toType)))  return true;
        if (this.isBoolean() && (((ClassType)toType).fullName().equals("java.lang.Boolean") || ts.BooleanWrapper().isSubtype(toType)) ) return true;
        if (this.isByte() && (((ClassType)toType).fullName().equals("java.lang.Byte") || ts.ByteWrapper().isSubtype(toType))) return true;
        if (this.isShort() && (((ClassType)toType).fullName().equals("java.lang.Short") || ts.ShortWrapper().isSubtype(toType))) return true;
        if (this.isChar() && (((ClassType)toType).fullName().equals("java.lang.Character") || ts.CharacterWrapper().isSubtype(toType))) return true;
        if (this.isLong() && (((ClassType)toType).fullName().equals("java.lang.Long") || ts.LongWrapper().isSubtype(toType))) return true;
        if (this.isDouble() && (((ClassType)toType).fullName().equals("java.lang.Double") || ts.DoubleWrapper().isSubtype(toType))) return true;
        if (this.isFloat() && (((ClassType)toType).fullName().equals("java.lang.Float") || ts.FloatWrapper().isSubtype(toType))) return true;
        return false;
    }
}
