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
    ClassType Iterable();
    ClassType Iterator();

    ClassType IntegerWrapper();
    ClassType ByteWrapper();
    ClassType ShortWrapper();
    ClassType BooleanWrapper();
    ClassType CharacterWrapper();
    ClassType LongWrapper();
    ClassType DoubleWrapper();
    ClassType FloatWrapper();

    PrimitiveType primitiveOf(Type t);
    ClassType classOf(Type t);
    boolean equivalent(TypeObject t1, TypeObject t2);
    boolean alreadyInResultSet(Set results, Type t);
    boolean isAutoEquivalent(Type t1, Type t2);
    public boolean isNumericWrapper(Type t);
    public boolean isIntOrLessWrapper(Type t1);

    EnumInstance enumInstance(Position pos, ClassType ct, Flags f,  String name, ParsedClassType anonType);
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

    public ArrayType arrayType(Position pos, Type base);
    /*public void handleTypeRestrictions(List typeVariables, List typeArguments) throws SemanticException;
    public void resetTypeRestrictions(List typeVariables, List typeArguments) throws SemanticException;*/

    public Type findRequiredType(IntersectionType iType, ParameterizedType pType);
    public boolean equals(TypeObject arg1, TypeObject arg2);
    public AnyType anyType();
    public AnySuperType anySuperType(Type t);
    public AnySubType anySubType(Type t);

    public boolean isEquivalent(TypeObject arg1, TypeObject arg2);


    // type inferrence for gen methods
    public MethodInstance findGenericMethod(ClassType container, String name, List argTypes, ClassType currClass, List inferredTypes) throws SemanticException;
    public boolean genericMethodCallValid(MethodInstance mi, String name, List argTypes, List inferredTypes);
    public boolean genericCallValid(ProcedureInstance pi, List argTypes, List inferredTypes);
    public List allAncestorsOf(ReferenceType rt);
    public SyntheticType syntheticType(List common);

    public Type deduceInferredType(ParameterizedType ft, ParameterizedType actual, IntersectionType iType);
    public void updateInferred(int pos, Type infType, List inferred) throws SemanticException;
    public List inferTypesFromArgs(List typeVariables, List formals, List args, List inferred) throws SemanticException;

    void sortAnnotations(List annots, List runtimeAnnots, List classAnnots, List sourceAnnots);
    public boolean needsUnboxing(Type to, Type from);
    public boolean needsBoxing(Type to, Type from);

    public Set superTypesOf(ReferenceType t);
}
