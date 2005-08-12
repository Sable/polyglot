package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.util.*;

public interface JL5TypeSystem extends TypeSystem {
    // TODO: declare any new methods needed
    //polyglot.ext.jl5.types.LazyClassInitializer defaultClassInitializer();
    ParsedClassType createClassType(LazyClassInitializer init, Source fromSource);
    ClassType Enum();

    EnumInstance enumInstance(Position pos, ClassType ct, Flags f,  String name);
    Context createContext();

    EnumInstance findEnumConstant(ReferenceType container, String name, ClassType currClass) throws SemanticException;

    EnumInstance findEnumConstant(ReferenceType container, String name, Context c) throws SemanticException;
    
    EnumInstance findEnumConstant(ReferenceType container, String name) throws SemanticException;
   
    FieldInstance findFieldOrEnum(ReferenceType container, String name, ClassType currClass) throws SemanticException;
}
