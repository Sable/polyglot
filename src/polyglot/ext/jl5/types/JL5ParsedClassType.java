package polyglot.ext.jl5.types;

import polyglot.ext.jl5.ast.*;
import java.util.*;
import polyglot.types.*;

public interface JL5ParsedClassType extends ParsedClassType{
    void addEnumConstant(EnumInstance ei);
    List enumConstants();
    EnumInstance enumConstantNamed(String name);
    
    void addAnnotationElem(AnnotationElemInstance ai);
    List annotationElems();
    AnnotationElemInstance annotationElemNamed(String name);

    void annotations(List annotations);
    List annotations();

    List typeVariables();
    void addTypeVariable(IntersectionType type);

    void typeVariables(List vars);
    
    boolean hasTypeVariable(String name);
    IntersectionType getTypeVariable(String name);

    boolean isGeneric();

    LazyClassInitializer init();

    boolean equivalentImpl(TypeObject arg2);
    /*List typeArguments();
    void typeArguments(List args);

    boolean isParameterized();*/

}
