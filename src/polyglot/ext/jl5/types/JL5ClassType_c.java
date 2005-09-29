package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.types.*;
import polyglot.ast.*;

public abstract class JL5ClassType_c extends ClassType_c implements JL5ClassType {

    public abstract List enumConstants();
    
    public EnumInstance enumConstantNamed(String name){
        for(Iterator it = enumConstants().iterator(); it.hasNext();){
            EnumInstance ei = (EnumInstance)it.next();
            if (ei.name().equals(name)){
                return ei;
            }
        }
        return null;
    }

    public boolean isImplicitCastValidImpl(Type toType){
        if (isAutoUnboxingValid(toType)) return true;
        if (toType instanceof IntersectionType){
            return isClassToIntersectionValid(toType);
        }
        return super.isImplicitCastValidImpl(toType);
    }

    private boolean isClassToIntersectionValid(Type toType){
        IntersectionType it = (IntersectionType)toType;
        if (it.bounds() == null || it.bounds().isEmpty()) return true;
        return ts.isImplicitCastValid(this, ((TypeNode)it.bounds().get(0)).type());
    }
    
    private boolean isAutoUnboxingValid(Type toType){
        
        if (!toType.isPrimitive()) return false;
        if (toType.isInt() && this.fullName().equals("java.lang.Integer")) return true;
        if (toType.isBoolean() && this.fullName().equals("java.lang.Boolean")) return true;
        if (toType.isByte() && this.fullName().equals("java.lang.Byte")) return true;
        if (toType.isShort() && this.fullName().equals("java.lang.Short")) return true;
        if (toType.isChar() && this.fullName().equals("java.lang.Character")) return true;
        if (toType.isLong() && this.fullName().equals("java.lang.Long")) return true;
        if (toType.isDouble() && this.fullName().equals("java.lang.Double")) return true;
        if (toType.isFloat() && this.fullName().equals("java.lang.Float")) return true;
        return false;
    }
}
