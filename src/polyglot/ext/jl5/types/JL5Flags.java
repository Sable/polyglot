package polyglot.ext.jl5.types;

import polyglot.types.*;

public class JL5Flags extends Flags {
    public static final Flags ENUM = createFlag("enum", null);

    public JL5Flags(){
        super(0L);
    }
    
    public static Flags setEnumModifier(Flags f){
        return f.set(ENUM);
    }

    public static Flags clearEnumModifier(Flags f){
        return f.clear(ENUM);
    }

    public static boolean isEnumModifier(Flags f){
        return f.contains(ENUM);
    }
}
