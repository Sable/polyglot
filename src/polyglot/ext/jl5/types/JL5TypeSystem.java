package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.util.*;
import java.util.*;
import polyglot.ast.*;
import polyglot.ext.jl5.ast.*;

public interface JL5TypeSystem extends TypeSystem {
    // TODO: declare any new methods needed
    //polyglot.ext.jl5.types.LazyClassInitializer defaultClassInitializer();
    ParsedClassType createClassType(LazyClassInitializer init, Source fromSource);
    ParsedClassType createClassType(Source fromSource);

    ClassType Enum();
    
    ClassType Annotation();

    ClassType IntegerWrapper();
    ClassType ByteWrapper();
    ClassType ShortWrapper();
    ClassType BooleanWrapper();
    ClassType CharacterWrapper();
    ClassType LongWrapper();
    ClassType DoubleWrapper();
    ClassType FloatWrapper();

    EnumInstance enumInstance(Position pos, ClassType ct, Flags f,  String name);
    AnnotationElemInstance annotationElemInstance(Position pos, ClassType ct, Flags f, Type type,  String name, boolean hasDefault);

    Context createContext();

    EnumInstance findEnumConstant(ReferenceType container, String name, ClassType currClass) throws SemanticException;
    
    AnnotationElemInstance findAnnotation(ReferenceType container, String name, ClassType currClass) throws SemanticException;

    EnumInstance findEnumConstant(ReferenceType container, String name, Context c) throws SemanticException;
    
    EnumInstance findEnumConstant(ReferenceType container, String name) throws SemanticException;
   
    FieldInstance findFieldOrEnum(ReferenceType container, String name, ClassType currClass) throws SemanticException;

    boolean isValidAnnotationValueType(Type t);

    boolean numericConversionBaseValid(Type t, Object value);
    boolean isBaseCastValid(Type from, Type to);

    void checkDuplicateAnnotations(List annotations) throws SemanticException;
    void checkValueConstant(Expr value) throws SemanticException;

    Flags flagsForBits(int bits);
    
    public void checkAnnotationApplicability(AnnotationElem annotation, Node n) throws SemanticException;
   
    public IntersectionType intersectionType(Position pos, String name, List bounds);
    public ParameterizedType parameterizedType(JL5ParsedClassType type);
    
    public void handleTypeRestrictions(List typeVariables, List typeArguments) throws SemanticException;
    public void resetTypeRestrictions(List typeVariables, List typeArguments) throws SemanticException;

    public Type findRequiredType(IntersectionType iType, ParameterizedType pType);
}
