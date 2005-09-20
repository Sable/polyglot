package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.ext.jl.types.*;
import polyglot.types.Flags;
import polyglot.types.SemanticException;

public class JL5TypeSystem_c extends TypeSystem_c implements JL5TypeSystem {
    // TODO: implement new methods in JL5TypeSystem.
    // TODO: override methods as needed from TypeSystem_c.

    protected final Flags TOP_LEVEL_CLASS_FLAGS = JL5Flags.setEnumModifier(super.TOP_LEVEL_CLASS_FLAGS);

    public void checkTopLevelClassFlags(Flags f) throws SemanticException {
        System.out.println(TOP_LEVEL_CLASS_FLAGS);
        System.out.println(f.clear(TOP_LEVEL_CLASS_FLAGS).equals(Flags.NONE));
        System.out.println(f.clear(TOP_LEVEL_CLASS_FLAGS));
        System.out.println(Flags.NONE);
        System.out.println("f: "+f);
        if (! f.clear(TOP_LEVEL_CLASS_FLAGS).equals(Flags.NONE)) {
            throw new SemanticException(
                "Cannot declare a top-level class with flag(s) " +
                f.clear(TOP_LEVEL_CLASS_FLAGS) + ".");
        }
        
        if (f.isFinal() && f.isInterface()) {
            throw new SemanticException("Cannot declare a final interface.");
        }
                
        checkAccessFlags(f);
    }

    public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource){
        System.out.println("creating JL5 ParsedClassType");
        return new JL5ParsedClassType_c(this, init, fromSource);
    }

    /*public*/ /*polyglot.ext.jl5.types.*//*LazyClassInitializer defaultClassInitializer(){
        if (defaultClassInit == null){
            defaultClassInit = new LazyClassInitializer_c(this);
        }
        return defaultClassInit;
    }*/
}
