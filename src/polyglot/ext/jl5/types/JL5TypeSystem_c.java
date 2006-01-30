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

import java.lang.reflect.Modifier;

import polyglot.frontend.*;
import polyglot.ext.jl.types.*;
import polyglot.ext.jl5.ast.*;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import java.util.*;

public class JL5TypeSystem_c extends TypeSystem_c implements JL5TypeSystem {
    // TODO: implement new methods in JL5TypeSystem.
    // TODO: override methods as needed from TypeSystem_c.

    protected ClassType ENUM_;
    protected ClassType ANNOTATION_;
    
    // this is for extended for
    protected ClassType ITERABLE_;
    protected ClassType ITERATOR_;
    
    public ClassType Enum() { 
        if (ENUM_ != null) {
            return ENUM_;
        }
        else {
            return ENUM_ = load("java.lang.Enum");
        }
    }
    
    public ClassType Annotation() { 
        if (ANNOTATION_ != null) {
            return ANNOTATION_;
        }
        else {
            return ANNOTATION_ = load("java.lang.annotation.Annotation");
        }
    }
  
    public ClassType Iterable() { 
        if (ITERABLE_ != null) {
            return ITERABLE_;
        }
        else {
            return ITERABLE_ = load("java.lang.Iterable");
        }
    }
  
    public ClassType Iterator() { 
        if (ITERATOR_ != null) {
            return ITERATOR_;
        }
        else {
            return ITERATOR_ = load("java.util.Iterator");
        }
    }
  
    protected ClassType INTEGER_WRAPPER;
    protected ClassType BYTE_WRAPPER;
    protected ClassType SHORT_WRAPPER;
    protected ClassType CHARACTER_WRAPPER;
    protected ClassType BOOLEAN_WRAPPER;
    protected ClassType LONG_WRAPPER;
    protected ClassType DOUBLE_WRAPPER;
    protected ClassType FLOAT_WRAPPER;

    public ClassType IntegerWrapper(){
        if (INTEGER_WRAPPER != null){
            return INTEGER_WRAPPER;
        }   
        else {
            return INTEGER_WRAPPER = load("java.lang.Integer");
        }
    }
    
    public ClassType ByteWrapper(){
        if (BYTE_WRAPPER != null){
            return BYTE_WRAPPER;
        }   
        else {
            return BYTE_WRAPPER = load("java.lang.Byte");
        }
    }
    
    public ClassType ShortWrapper(){
        if (SHORT_WRAPPER != null){
            return SHORT_WRAPPER;
        }   
        else {
            return SHORT_WRAPPER = load("java.lang.Short");
        }
    }
    

    public ClassType BooleanWrapper(){
        if (BOOLEAN_WRAPPER != null){
            return BOOLEAN_WRAPPER;
        }   
        else {
            return BOOLEAN_WRAPPER = load("java.lang.Boolean");
        }
    }
    
    public ClassType CharacterWrapper(){
        if (CHARACTER_WRAPPER != null){
            return CHARACTER_WRAPPER;
        }   
        else {
            return CHARACTER_WRAPPER = load("java.lang.Character");
        }
    }
    
    public ClassType LongWrapper(){
        if (LONG_WRAPPER != null){
            return LONG_WRAPPER;
        }   
        else {
            return LONG_WRAPPER = load("java.lang.Long");
        }
    }
    
    public ClassType DoubleWrapper(){
        if (DOUBLE_WRAPPER != null){
            return DOUBLE_WRAPPER;
        }   
        else {
            return DOUBLE_WRAPPER = load("java.lang.Double");
        }
    }
    
    public ClassType FloatWrapper(){
        if (FLOAT_WRAPPER != null){
            return FLOAT_WRAPPER;
        }   
        else {
            return FLOAT_WRAPPER = load("java.lang.Float");
        }
    }
  
    public PrimitiveType primitiveOf(Type t){
        if (t.isPrimitive()) return (PrimitiveType)t;
        if (equals(t, FloatWrapper())) return Float();
        if (equals(t, DoubleWrapper())) return Double();
        if (equals(t, LongWrapper())) return Long();
        if (equals(t, IntegerWrapper())) return Int();
        if (equals(t, ShortWrapper())) return Short();
        if (equals(t, ByteWrapper())) return Byte();
        if (equals(t, CharacterWrapper())) return Char();
        return null;
    }
    
    public ClassType classOf(Type t){
        if (t.isClass()) return (ClassType)t;
        if (equals(t, Float())) return FloatWrapper();
        if (equals(t, Double())) return DoubleWrapper();
        if (equals(t, Long())) return LongWrapper();
        if (equals(t, Int())) return IntegerWrapper();
        if (equals(t, Short())) return ShortWrapper();
        if (equals(t, Byte())) return ByteWrapper();
        if (equals(t, Char())) return CharacterWrapper();
        return null;
    }
    
    public boolean isAutoEquivalent(Type t1, Type t2){
        if (t1.isPrimitive()){
            if (t1.isInt() && equals(INTEGER_WRAPPER, t2)) return true;
            if (t1.isByte() && equals(BYTE_WRAPPER, t2)) return true;
            if (t1.isShort() && equals(SHORT_WRAPPER, t2)) return true;
            if (t1.isChar() && equals(CHARACTER_WRAPPER, t2)) return true;
            if (t1.isBoolean() && equals(BOOLEAN_WRAPPER, t2)) return true;
            if (t1.isLong() && equals(LONG_WRAPPER, t2)) return true;
            if (t1.isDouble() && equals(DOUBLE_WRAPPER, t2)) return true;
            if (t1.isFloat() && equals(FLOAT_WRAPPER, t2)) return true;
        }
        else if (t2.isPrimitive()){
            if (t2.isInt() && equals(INTEGER_WRAPPER, t1)) return true;
            if (t2.isByte() && equals(BYTE_WRAPPER, t1)) return true;
            if (t2.isShort() && equals(SHORT_WRAPPER, t1)) return true;
            if (t2.isChar() && equals(CHARACTER_WRAPPER, t1)) return true;
            if (t2.isBoolean() && equals(BOOLEAN_WRAPPER, t1)) return true;
            if (t2.isLong() && equals(LONG_WRAPPER, t1)) return true;
            if (t2.isDouble() && equals(DOUBLE_WRAPPER, t1)) return true;
            if (t2.isFloat() && equals(FLOAT_WRAPPER, t1)) return true;
        }
        return false;
    }

    public boolean isNumericWrapper(Type t){
        if (equals(INTEGER_WRAPPER, t) || equals(BYTE_WRAPPER, t) || equals(SHORT_WRAPPER, t) || equals(CHARACTER_WRAPPER, t) || equals(LONG_WRAPPER, t) || equals(DOUBLE_WRAPPER, t) || equals(FLOAT_WRAPPER, t)) return true;
        return false;
    }
   
    public boolean isIntOrLessWrapper(Type t){
        if (equals(INTEGER_WRAPPER, t) || equals(BYTE_WRAPPER, t) || equals(SHORT_WRAPPER, t) || equals(CHARACTER_WRAPPER, t) ) return true;
        return false;
    }
    
    protected final Flags TOP_LEVEL_CLASS_FLAGS = JL5Flags.setAnnotationModifier(JL5Flags.setEnumModifier(super.TOP_LEVEL_CLASS_FLAGS));

    protected final Flags MEMBER_CLASS_FLAGS = JL5Flags.setAnnotationModifier(JL5Flags.setEnumModifier(super.MEMBER_CLASS_FLAGS));
    

    public void checkTopLevelClassFlags(Flags f) throws SemanticException {
      	if (! f.clear(TOP_LEVEL_CLASS_FLAGS).equals(JL5Flags.NONE)) {
	    throw new SemanticException(
		"Cannot declare a top-level class with flag(s) " +
		f.clear(TOP_LEVEL_CLASS_FLAGS) + ".");
	}

        if (f.isFinal() && f.isInterface()) {
            throw new SemanticException("Cannot declare a final interface.");
        }

	checkAccessFlags(f);
    }

    public void checkMemberClassFlags(Flags f) throws SemanticException {
      	if (! f.clear(MEMBER_CLASS_FLAGS).equals(JL5Flags.NONE)) {
	    throw new SemanticException(
		"Cannot declare a member class with flag(s) " +
		f.clear(MEMBER_CLASS_FLAGS) + ".");
	}

        if (f.isStrictFP() && f.isInterface()) {
            throw new SemanticException("Cannot declare a strictfp interface.");
        }

        if (f.isFinal() && f.isInterface()) {
            throw new SemanticException("Cannot declare a final interface.");
        }

	checkAccessFlags(f);
    }

    public ConstructorInstance defaultConstructor(Position pos, ClassType container){
        assert_(container);

        Flags access = Flags.NONE;

        if (container.flags().isPrivate() || JL5Flags.isEnumModifier(container.flags())){
            access = access.Private();
        }
        if (container.flags().isProtected()){
            access = access.Protected();
        }
        if (container.flags().isPublic() && !JL5Flags.isEnumModifier(container.flags())){
            access = access.Public();
        }
        return constructorInstance(pos, container, access, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        

    }
    
    public LazyClassInitializer defaultClassInitializer(){
        if (defaultClassInit == null){
            defaultClassInit = new JL5LazyClassInitializer_c(this);
        }
        return defaultClassInit;
    }
    
    public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource){
        return new JL5ParsedClassType_c(this, init, fromSource);
    }

    protected PrimitiveType createPrimitive(PrimitiveType.Kind kind){
        return new JL5PrimitiveType_c(this, kind);
    }
    
    public void checkClassConformance(ClassType ct) throws SemanticException {
  
        if (JL5Flags.isEnumModifier(ct.flags())){
            // check enums elsewhere - have to do something special with
            // abstract methods and anon enum element bodies
            //return;
            JL5ParsedClassType pct = (JL5ParsedClassType)ct;
            List enumConsts = pct.enumConstants();
            boolean allAnonNull = true;
            for (Iterator it = enumConsts.iterator(); it.hasNext(); ){
                EnumInstance ei = (EnumInstance)it.next();
                if (ei.anonType() != null){
                    allAnonNull = false;
                    break;
                }
            }

            if (allAnonNull){
                super.checkClassConformance(ct);
            }
            else {
                // if enum type declares abstract method ensure
                // !!every!! enum constant decl declares anon body
                // and !!every!! body implements this abstract
                // method
                for (Iterator it = ct.methods().iterator(); it.hasNext(); ){
                    MethodInstance mi = (MethodInstance)it.next();
                    if (!mi.flags().isAbstract()) continue;
                    for (Iterator jt = enumConsts.iterator(); jt.hasNext(); ){
                        EnumInstance ei = (EnumInstance)jt.next();
                        if (ei.anonType() == null){
                            throw new SemanticException("Enum constant decl: "+ei.name()+" must delclare an anonymous subclass of: "+ct+" and implement the abstract method: "+mi, ei.position());
                        }
                        else {
                            boolean implFound = false;
                            for (Iterator kt = ei.anonType().methods().iterator(); kt.hasNext(); ){
                                MethodInstance mj = (MethodInstance)kt.next();
                                if (canOverride(mj, mi)){
                                    implFound = true;
                                }
                            }
                            if (!implFound){
                                throw new SemanticException("Enum constant decl anonymous subclass must implement method: "+mi, ei.position());
                            }
                        }
                    }
                }

                // still need to check superInterfaces to ensure this 
                // class implements the methods except they can be 
                // abstract (previous checks ensure okay)
                List superInterfaces = abstractSuperInterfaces(ct);
                for (Iterator it = superInterfaces.iterator(); it.hasNext(); ){
                    ReferenceType rt = (ReferenceType)it.next();
                    if (equals(rt, ct)) continue;
                    for (Iterator jt = rt.methods().iterator(); jt.hasNext();){
                        MethodInstance mi = (MethodInstance)jt.next();
                        if (!mi.flags().isAbstract()) continue;
                        
                        boolean implFound = false;
                        // don't need to look in super classes as the only
                        // one is java.lang.Enum so just look here and 
                        // there
                        for (Iterator kt = ct.methods().iterator(); kt.hasNext(); ){
                            MethodInstance mj = (MethodInstance)kt.next();
                            if ((canOverride(mj, mi))){
                                implFound = true;
                                break;
                            }
                        }
                        for (Iterator kt = ct.superType().toReference().methods().iterator(); kt.hasNext(); ){
                            MethodInstance mj = (MethodInstance)kt.next();
                            if ((canOverride(mj, mi))){
                                implFound = true;
                                break;
                            }
                        }

                        if (!implFound){
                            throw new SemanticException(ct.fullName()+" should be declared abstract: it does not define: "+mi.signature()+", which is declared in "+rt.toClass().fullName(), ct.position());
                        }
                    }
                }
            }
        }
        else {
            super.checkClassConformance(ct); 
        }
    }

    public EnumInstance findEnumConstant(ReferenceType container, String name, Context c) throws SemanticException {
        ClassType ct = null;
        if (c != null) ct = c.currentClass();
        return findEnumConstant(container, name, ct);
    }
    
    public EnumInstance findEnumConstant(ReferenceType container, String name, ClassType currClass) throws SemanticException {
        Collection enumConstants = findEnumConstants(container, name);
        if (enumConstants.size() == 0){
            throw new NoMemberException(JL5NoMemberException.ENUM_CONSTANT, 
                    "Enum Constant: \"" + name+"\" not found in type \"" +
                    container + "\".");
        }
        Iterator i = enumConstants.iterator();
        EnumInstance ei = (EnumInstance) i.next();

        if (i.hasNext()){
            EnumInstance ei2 = (EnumInstance) i.next();
            
            throw new SemanticException("Enum Constant \""+name+ "\" is ambiguous; it is defined in both "+ ei.container() + " and " + ei2.container() +".");
        }

        if (currClass != null && !isAccessible(ei, currClass)){
            throw new SemanticException("Cannot access "+ei+".");
        }

        return ei;
    }
    
    public AnnotationElemInstance findAnnotation(ReferenceType container, String name, ClassType currClass) throws SemanticException {
        Collection annotations = findAnnotations(container, name);
        if (annotations.size() == 0){
            throw new NoMemberException(JL5NoMemberException.ANNOTATION, 
                    "Annotation: \"" + name+"\" not found in type \"" +
                    container + "\".");
        }
        Iterator i = annotations.iterator();
        AnnotationElemInstance ai = (AnnotationElemInstance) i.next();

        if (i.hasNext()){
            AnnotationElemInstance ai2 = (AnnotationElemInstance) i.next();
            
            throw new SemanticException("Annotation \""+name+ "\" is ambiguous; it is defined in both "+ ai.container() + " and " + ai2.container() +".");
        }

        if (currClass != null && !isAccessible(ai, currClass)){
            throw new SemanticException("Cannot access "+ai+".");
        }
        return ai;
    }
    

    public EnumInstance findEnumConstant(ReferenceType container, String name) throws SemanticException {
        return findEnumConstant(container, name, (ClassType) null);
    }
   
    
    public Set findEnumConstants(ReferenceType container, String name) {
        assert_(container);
        if (container == null){
            throw new InternalCompilerError("Cannot access enum constant \"" + name + "\" within a null container type.");
        }
        EnumInstance ei = null;
        
        if (container instanceof JL5ParsedClassType){
            ei = ((JL5ParsedClassType)container).enumConstantNamed(name);
        }

        if (ei != null){
            return Collections.singleton(ei);
        }

        Set enumConstants = new HashSet();

        return enumConstants;
    }
    
    public Set findAnnotations(ReferenceType container, String name) {
        assert_(container);
        if (container == null){
            throw new InternalCompilerError("Cannot access annotation \"" + name + "\" within a null container type.");
        }
        AnnotationElemInstance ai = ((JL5ParsedClassType)container).annotationElemNamed(name);

        if (ai != null){
            return Collections.singleton(ai);
        }

        Set annotations = new HashSet();

        return annotations;
    }
    
    public EnumInstance enumInstance(Position pos, ClassType ct, Flags f, String name, ParsedClassType anonType){
        assert_(ct);
        return new EnumInstance_c(this, pos, ct, f, name, anonType);
    }

    public AnnotationElemInstance annotationElemInstance(Position pos, ClassType ct, Flags f, Type type,  String name, boolean hasDefault){
        assert_(ct);
        assert_(type);
        return new AnnotationElemInstance_c(this, pos, ct, f, type, name, hasDefault);
    }

    public Context createContext(){
        return new JL5Context_c(this);
    }

    public FieldInstance findFieldOrEnum(ReferenceType container, String name, ClassType currClass) throws SemanticException {
    
        FieldInstance fi = null;
       
        try {
            fi = findField(container, name, currClass);
        }
        catch (NoMemberException e){
            fi = findEnumConstant(container, name, currClass);
        }
            
        return fi;
    }

    public MethodInstance methodInstance(Position pos, ReferenceType container, Flags flags, Type returnType, String name, List argTypes, List excTypes) {

         assert_(container);
         assert_(returnType);
         assert_(argTypes);
         assert_(excTypes);
         return new JL5MethodInstance_c(this, pos, container, flags, returnType, name, argTypes, excTypes);
    }
    public ConstructorInstance constructorInstance(Position pos, ClassType container, Flags flags, List argTypes, List excTypes) {

         assert_(container);
         assert_(argTypes);
         assert_(excTypes);
         return new JL5ConstructorInstance_c(this, pos, container, flags, argTypes, excTypes);
    }

    public IntersectionType intersectionType(Position pos, String name, List bounds){
        assert_(bounds);
        return new IntersectionType_c(this, pos, name, bounds);
    }

    public SyntheticType syntheticType(List bounds){
        assert_(bounds);
        return new SyntheticType_c(this, bounds);
    }
    
    public ParameterizedType parameterizedType(JL5ParsedClassType ct){
        
        return new ParameterizedType_c(ct);
    }
   
    public boolean isValidAnnotationValueType(Type t) {
        // must be one of primitive, String, Class, enum, annotation or 
        // array of one of these
        if (t.isPrimitive()) return true;
        if (t instanceof JL5ParsedClassType){
            if (JL5Flags.isEnumModifier(((JL5ParsedClassType)t).flags())) return true;
            if (JL5Flags.isAnnotationModifier(((JL5ParsedClassType)t).flags())) return true;
            if (((JL5ParsedClassType)t).fullName().equals("java.lang.String")) return true;
            if (((JL5ParsedClassType)t).fullName().equals("java.lang.Class")) return true;
        }
        if (t.isArray()){
            return isValidAnnotationValueType(((ArrayType)t).base());
        }
        return false;
    }

    public boolean isBaseCastValid(Type fromType, Type toType){
        if (toType.isArray()){
            Type base = ((ArrayType)toType).base();
            assert_(base);
            return fromType.isImplicitCastValidImpl(base);
        }
        return false;
    }


    public boolean numericConversionBaseValid(Type t, Object value){
        if (t.isArray()){
            return super.numericConversionValid(((ArrayType)t).base(), value);
        }
        return false;
    }
    
    public void checkDuplicateAnnotations(List annotations) throws SemanticException {
        // check no duplicate annotations used
        ArrayList l = new ArrayList(annotations);
        for (int i = 0; i < l.size(); i++){
            AnnotationElem ai = (AnnotationElem)l.get(i);
            for (int j = i+1; j < l.size(); j++){
                AnnotationElem aj = (AnnotationElem)l.get(j);
                if (ai.typeName().type() == aj.typeName().type()){
                    throw new SemanticException("Duplicate annotation use: "+aj.typeName(), aj.position());
                }
            }
        }
    }

    public void checkValueConstant(Expr value) throws SemanticException {
        if (value instanceof ArrayInit) {
            // check elements
            for (Iterator it = ((ArrayInit)value).elements().iterator(); it.hasNext(); ){
                Expr next = (Expr)it.next();
                if ((!next.isConstant() || next == null || next instanceof NullLit) && !(next instanceof ClassLit)){
                    throw new SemanticException("Annotation attribute value must be constant", value.position());
                }
            }
        }
        else if ((!value.isConstant() || value == null || value instanceof NullLit) && !(value instanceof ClassLit)){
            // for purposes of annotation elems class lits are constants
            throw new SemanticException("Annotation attribute value must be constant", value.position());
        }
    }

    public Flags flagsForBits(int bits){
        Flags f = super.flagsForBits(bits);
        if ((bits & JL5Flags.ANNOTATION_MOD) != 0) f = JL5Flags.setAnnotationModifier(f);
        if ((bits & JL5Flags.ENUM_MOD) != 0) {
            f = JL5Flags.setEnumModifier(f);
        }
        return f;
    }

    public void checkAnnotationApplicability(AnnotationElem annotation, Node n) throws SemanticException {
        List applAnnots = ((JL5ParsedClassType)annotation.typeName().type()).annotations();
        // if there are no annotations applied to this annotation type then 
        // there is no need to check the target type of the annotation
        if (applAnnots != null) {
        
        for (Iterator it = applAnnots.iterator(); it.hasNext(); ){
            AnnotationElem next = (AnnotationElem)it.next();
            if (((ClassType)next.typeName().type()).fullName().equals("java.lang.annotation.Target")) {
                if (next instanceof NormalAnnotationElem){
                    for (Iterator elems = ((NormalAnnotationElem)next).elements().iterator(); elems.hasNext(); ){
                        ElementValuePair elemVal = (ElementValuePair)elems.next();
                        if (elemVal.value() instanceof JL5Field){
                            String check = ((JL5Field)elemVal.value()).name();
                            appCheckValue(check, n);
                        }
                        else if (elemVal.value() instanceof ArrayInit){
                            ArrayInit val = (ArrayInit)elemVal.value();
                            if (val.elements().isEmpty()){
                                // automatically throw exception
                                // this annot cannot be applied anywhere
                                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
                            }
                            else {
                                for (Iterator vals = val.elements().iterator(); vals.hasNext(); ){
                                    Object nextVal = vals.next();
                                    if (nextVal instanceof JL5Field){
                                        String valCheck = ((JL5Field)nextVal).name();
                                        appCheckValue(valCheck, n);
                                    }
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
        }
        if (((ClassType)annotation.typeName().type()).fullName().equals("java.lang.Override")) {
            appCheckOverride(n);
        }
    }

    private void appCheckValue(String val, Node n) throws SemanticException {
        if (val.equals("ANNOTATION_TYPE")){
            if (!(n instanceof ClassDecl) || !JL5Flags.isAnnotationModifier(((ClassDecl)n).flags())){
                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
            }
        }
        else if (val.equals("CONSTRUCTOR")){
            if (!(n instanceof ConstructorDecl)) {
                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
            }
        }
        else if (val.equals("FIELD")){
            if (!(n instanceof FieldDecl)) {
                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
            }
        }
        else if (val.equals("LOCAL_VARIABLE")){
            if (!(n instanceof LocalDecl)) {
                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
            }
        }
        else if (val.equals("METHOD")){
            if (!(n instanceof MethodDecl)) {
                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
            }
        }
        else if (val.equals("PACKAGE")){
        }
        else if (val.equals("PARAMETER")){
            if (!(n instanceof Formal)) {
                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
            }
        }
        else if (val.equals("TYPE")){
            if (!(n instanceof ClassDecl)){
                throw new SemanticException("Annotation type not applicable to this kind of declaration", n.position());
            }
        }
    }

    private void appCheckOverride(Node n) throws SemanticException{
        MethodDecl md = (MethodDecl)n; // the other check should 
                                       // prevent anything else
        JL5ParsedClassType mdClass = (JL5ParsedClassType)md.methodInstance().container();
        try {
            MethodInstance mi = findMethod((ReferenceType)mdClass.superType(), md.name(), md.methodInstance().formalTypes(), (ClassType)mdClass.superType());
        }
        catch (NoMemberException e){
            throw new SemanticException("method does not override a method from its superclass", md.position());
        }
    }
    
    
    public Type findRequiredType(IntersectionType iType, ParameterizedType pType){
        String id = iType.name();

        // maybe its a type variable in the raw type of pType
        // then the required type is the corresponding argument type in pType

        if (pType.isGeneric()){
            for (int i = 0; i < pType.typeVariables().size(); i++){
               if (((IntersectionType)pType.typeVariables().get(i)).name().equals(iType.name())){
                    Type required = (Type)pType.typeArguments().get(i);
                    /*if (required instanceof AnySuperType){
                        ((AnySuperType)required).upperBound(iType.upperBound());
                    }
                    else if (required instanceof AnyType){           
                        ((AnyType)required).upperBound(iType.upperBound());
                    }*/
                    /*if (required instanceof AnyType){
                        IntersectionType typeVar = (IntersectionType)pType.typeVariables().get(i);      
                        return anySubType(typeVar.erasureType());
                    }*/
                    return required; 
                }
            }
        }
        if (((JL5ParsedClassType)pType.superType()).isGeneric()){
            for (int i = 0; i < ((JL5ParsedClassType)pType.superType()).typeVariables().size(); i++)
                if (((IntersectionType)((JL5ParsedClassType)pType.superType()).typeVariables().get(i)).name().equals(iType.name())){
                    Type result = (Type)((ParameterizedType)pType.superType()).typeArguments().get(i);
                    if (result instanceof IntersectionType){
                        if (pType.isGeneric()){
                            for (int j = 0; j < pType.typeVariables().size(); j++){
                               if (((IntersectionType)pType.typeVariables().get(j)).name().equals(((IntersectionType)result).name())){
                                    Type required = (Type)pType.typeArguments().get(j);
                                    /*if (required instanceof AnySuperType){
                                        ((AnySuperType)required).upperBound(iType.upperBound());
                                    }
                                    else if (required instanceof AnyType){           
                                        ((AnyType)required).upperBound(iType.upperBound());
                                    }*/
                                    /*if (required instanceof AnyType){
                                        IntersectionType typeVar = (IntersectionType)pType.typeVariables().get(j);      
                                        return anySubType(typeVar.erasureType());
                                    }*/
                                    return required; 
                               }
                            }
                        }
                    }
                    else {
                        return result;
                    }
                }
        }
        return iType.erasureType();
    }

    /*public boolean equals(TypeObject arg1, TypeObject arg2){
        if (arg1 instanceof ParameterizedType) return ((ParameterizedType)arg1).equalsImpl(arg2);
        if (arg1 instanceof IntersectionType) return ((IntersectionType)arg1).equalsImpl(arg2);
        if (arg1 instanceof AnySubType) return ((AnySubType)arg1).equalsImpl(arg2);
        if (arg1 instanceof AnySuperType) return ((AnySuperType)arg1).equalsImpl(arg2);
        return super.equals(arg1, arg2);
    }*/
    
    public boolean equivalent(TypeObject arg1, TypeObject arg2){
        if (arg1 instanceof ParameterizedType) return ((ParameterizedType)arg1).equivalentImpl(arg2);
        if (arg1 instanceof IntersectionType) return ((IntersectionType)arg1).equivalentImpl(arg2);
        if (arg1 instanceof AnySubType) return ((AnySubType)arg1).equivalentImpl(arg2);
        if (arg1 instanceof AnySuperType) return ((AnySuperType)arg1).equivalentImpl(arg2);
        if (arg1 instanceof JL5PrimitiveType) return ((JL5PrimitiveType)arg1).equivalentImpl(arg2);
        if (arg1 instanceof JL5ParsedClassType) return ((JL5ParsedClassType)arg1).equivalentImpl(arg2);
        return false;
    }
    
    public AnyType anyType(){
        return new AnyType_c(this);
    }
    public AnySuperType anySuperType(Type t){
        return new AnySuperType_c(this, t);
    }
    public AnySubType anySubType(Type t){
        return new AnySubType_c(this, t);
    }

    public ClassType findMemberClass(ClassType container, String name, ClassType currClass) throws SemanticException{
        if (container instanceof ParameterizedType){
            return super.findMemberClass(((ParameterizedType)container).baseType(), name, currClass);
        }
        return super.findMemberClass(container, name, currClass);
    }

    public Set findMemberClasses(ClassType container, String name) throws SemanticException {
       ClassType mt = container.memberClassNamed(name);

        if (mt != null) {
            if (! mt.isMember()) {
                throw new InternalCompilerError("Class " + mt +
                        " is not a member class, " +
                        " but is in " + container +
                        "\'s list of members.");
            }

            if ((mt.outer() != container) && (mt.outer() instanceof IntersectionType && !((IntersectionType)mt.outer()).bounds().contains(container))) {
                        
                
                throw new InternalCompilerError("Class " + mt +
                        " has outer class " +
                        mt.outer() +
                        " but is a member of " +
                        container);
            }

            return Collections.singleton(mt);
        }
        Set memberClasses = new HashSet();

        if (container.superType() != null) {
            Set s = findMemberClasses(container.superType().toClass(), name);
            memberClasses.addAll(s);
        }

        for (Iterator i = container.interfaces().iterator(); i.hasNext(); ) {
            Type it = (Type) i.next();

            Set s =  findMemberClasses(it.toClass(), name);
            memberClasses.addAll(s);
        }

        return memberClasses;

    }

    public ImportTable importTable(String sourceName, polyglot.types.Package pkg){
        assert_(pkg);
        return new JL5ImportTable(this, systemResolver, pkg, sourceName);
    }

    public ImportTable importTable(polyglot.types.Package pkg){
        assert_(pkg);
        return new JL5ImportTable(this, systemResolver, pkg);
    }

    public ArrayType arrayType(Position pos, Type type){
        return new JL5ArrayType_c(this, pos, type);
    }

    public boolean isEquivalent(TypeObject arg1, TypeObject arg2){
        if (arg1 instanceof ArrayType && arg2 instanceof ArrayType){
            return isEquivalent(((ArrayType)arg1).base(), ((ArrayType)arg2).base());
        }
        if (arg1 instanceof IntersectionType) {
            return ((IntersectionType)arg1).isEquivalent(arg2);
        }
        else if (arg2 instanceof IntersectionType){
            return ((IntersectionType)arg2).isEquivalent(arg1);
        }
        return this.equals(arg1, arg2);
    }
    
    // inferred types
    public MethodInstance findGenericMethod(ClassType container, String name, List argTypes, ClassType currClass, List inferredTypes) throws SemanticException {
        assert_(container);
        assert_(argTypes);
        List acceptable = findAcceptableGenericMethods(container, name, argTypes, currClass, inferredTypes);

        if (acceptable.size() == 0){
            throw new NoMemberException(NoMemberException.METHOD,
            "No valid method call found for " + name +
            "(" + listToString(argTypes) + ")" +
            " in " +
            container + ".");
        }

        MethodInstance mi = (MethodInstance)findProcedure(acceptable, container, argTypes, currClass);

        if (mi == null){
            throw new SemanticException("Reference to " + name +
            " is ambiguous, multiple methods match: "
            + acceptable);
        }
        return mi;
    }


    protected List findAcceptableGenericMethods(ReferenceType container, String name, List argTypes, ClassType currClass, List inferredTypes) throws SemanticException {
        assert_(container);
        assert_(argTypes);
        assert_(inferredTypes);
        List acceptable = new ArrayList();

        List unacceptable = new ArrayList();

        Set visitedTypes = new HashSet();
        LinkedList typeQueue = new LinkedList();
        typeQueue.addLast(container);

        while (!typeQueue.isEmpty()){
            Type type = (Type) typeQueue.removeFirst();

            if (visitedTypes.contains(type)) {
                continue;
            }

            visitedTypes.add(type);

            if (! type.isReference()) {
                throw new SemanticException("Cannot call method in " +
                " non-reference type " + type + ".");
            }

            for (Iterator i = type.toReference().methods().iterator(); i.hasNext(); ) {
                MethodInstance mi = (MethodInstance) i.next();


                if (genericMethodCallValid(mi, name, argTypes, inferredTypes)) {
                    if (isAccessible(mi, currClass)) {

                        acceptable.add(mi);
                    }
                    else {
                        // method call is valid, but the method is
                        // unacceptable.
                        unacceptable.add(mi);
                    }
                }
            }
            if (type.toReference().superType() != null) {
                typeQueue.addLast(type.toReference().superType());
            }

            typeQueue.addAll(type.toReference().interfaces());
        }

        // remove any method in acceptable that are overridden by an
        // unacceptable
        // method.
        for (Iterator i = unacceptable.iterator(); i.hasNext();) {
            MethodInstance mi = (MethodInstance)i.next();
            acceptable.removeAll(mi.overrides());
        }

        return acceptable;
 
    }

    public boolean genericMethodCallValid(MethodInstance prototype, String name, List argTypes, List inferredTypes){
        assert_(prototype);
        assert_(argTypes);
        assert_(inferredTypes);
        return ((JL5MethodInstance)prototype).genericMethodCallValidImpl(name, argTypes, inferredTypes);
    }

    public boolean genericCallValid(ProcedureInstance prototype, List argTypes, List inferredTypes){
        assert_(prototype);
        assert_(argTypes);
        assert_(inferredTypes);
        if (prototype instanceof JL5MethodInstance){
            return ((JL5MethodInstance)prototype).genericCallValidImpl(argTypes, inferredTypes);
        }
        /*else {
            return ((JL5ConstructorInstance)prototype).genericCallValidImpl(argTypes, inferredTypes);
        }*/
        return prototype.callValidImpl(argTypes);
    }
    
    public List allAncestorsOf(ReferenceType rt){
        List ancestors = new ArrayList();
        ReferenceType superT = (ReferenceType)rt.superType();
        if (superT != null){
            ancestors.addAll(allAncestorsOf(superT));
        }
        for (Iterator it = rt.interfaces().iterator(); it.hasNext(); ){
            ancestors.addAll(allAncestorsOf((ReferenceType)it.next()));
        }
        return ancestors;
    }
    
    public Type deduceInferredType(ParameterizedType ft, ParameterizedType actual, IntersectionType iType){
        Iterator it = ft.typeArguments().iterator();
        Iterator jt = actual.typeArguments().iterator();
        while (it.hasNext() && jt.hasNext()){
            Type iNext = (Type)it.next();
            Type jNext = (Type)jt.next();
            if (iNext instanceof IntersectionType && equals(iNext, iType)){
                return jNext;
            }
            else if (iNext instanceof ParameterizedType && jNext instanceof ParameterizedType){
                return deduceInferredType((ParameterizedType)iNext, (ParameterizedType)jNext, iType);
            }
        }
        return Object();
    }
    
    public void updateInferred(int pos, Type infType, List inferred) throws SemanticException {
        //ReferenceType refInf = (ReferenceType)infType;
        if (pos < inferred.size()){
            Type old = (Type)inferred.get(pos);
            // make new synthetic type of common supertypes of old and infType
            // or object if object only superType
            Type newType = null;
            if (isSubtype(old, infType)) {
                newType = infType;
            }
            else if (isSubtype(infType, old)){
                newType = old;
            }
            else{
                List common = new ArrayList();
                List allAncestorsOld = new ArrayList();
                if (old instanceof ReferenceType){
                    allAncestorsOld = allAncestorsOf((ReferenceType)old);
                }
                List allAncestorsInf = new ArrayList();
                if (infType instanceof ReferenceType){
                    allAncestorsInf = allAncestorsOf((ReferenceType)infType);
                }
                for (Iterator it = allAncestorsOld.iterator(); it.hasNext(); ){
                    Type t = (Type)it.next();
                    if (allAncestorsInf.contains(t)){
                        common.add(t);
                    }
                }
                if (common.size() == 0){
                    // this can happen when intersection with Null
                    newType = Object();
                }
                else if (common.size() == 1){
                    newType = (Type)common.get(0);
                }
                else {
                    newType = syntheticType(common);
                }
            }
            inferred.add(pos, newType);
        }
        else {
            inferred.add(pos, infType);
        }
    }
    
    public List inferTypesFromArgs(List typeVariables, List formalTypes, List argTypes, List inferred) throws SemanticException{
        for (int j = 0; j < typeVariables.size(); j++){
            IntersectionType iType = (IntersectionType) typeVariables.get(j);
            boolean found = false;
            for (int i = 0; i < formalTypes.size(); i++){
                Type ft = (Type)formalTypes.get(i);
                if (ft instanceof IntersectionType){
                    if (equals(ft, iType)){
                        updateInferred(j, (Type)argTypes.get(i), inferred);
                        found = true;
                    }
                }
                else if (ft instanceof ParameterizedType && argTypes.get(i) instanceof ParameterizedType){
                    if (((ParameterizedType)ft).comprisedOfIntersectionType(iType)){
                        updateInferred(j, deduceInferredType((ParameterizedType)ft, (ParameterizedType)argTypes.get(i), iType), inferred);
                        found = true;
                    }
                }
            }
            if (!found){
                inferred.add(Object());
            }
        
        }
        return inferred;
    }
   
    public void sortAnnotations(List annotations, List runtimeAnnotations, List classAnnotations, List sourceAnnotations){
        for (Iterator it = annotations.iterator(); it.hasNext(); ){
            AnnotationElem annot = (AnnotationElem)it.next();
            boolean sorted = false;
            List appliedAnnots = ((JL5ParsedClassType)annot.typeName().type()).annotations();
            if (appliedAnnots != null){
                for (Iterator jt = appliedAnnots.iterator(); jt.hasNext(); ){
                    AnnotationElem next = (AnnotationElem)jt.next();
                    if (((ClassType)next.typeName().type()).fullName().equals("java.lang.annotation.Retention")){
                        for (Iterator elems = ((NormalAnnotationElem)next).elements().iterator(); elems.hasNext(); ){
                            ElementValuePair elem = (ElementValuePair)elems.next();
                            if (elem.name().equals("value")){
                                if (elem.value() instanceof JL5Field){
                                    String val = ((JL5Field)elem.value()).name();
                                    if (val.equals("RUNTIME")){
                                        runtimeAnnotations.add(annot);
                                        sorted = true;
                                    }
                                    else if (val.equals("SOURCE")){
                                        sourceAnnotations.add(annot);
                                        sorted = true;
                                    }
                                    else{
                                        classAnnotations.add(annot);
                                        sorted = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!sorted){
                classAnnotations.add(annot);
            }
        }
    }

    public boolean needsUnboxing(Type to, Type from){
        if (to.isPrimitive() && from.isClass()) return true;
        return false;
    }

    public boolean needsBoxing(Type to, Type from){
        if (to.isClass() && from.isPrimitive()) return true;
        return false;
    }

    private List listOf(Type t){
        List l = new ArrayList();
        l.add(t);
        return l;
    }
    
    private List getSuperList(ReferenceType t){
        List result = new ArrayList();
        if (t instanceof AnyType || t instanceof AnySubType || t instanceof AnySuperType){
            return result;
        }
        //result.add(listOf(t));
        result.add(listOf(anySubType(t)));
        result.add(listOf(anySuperType(t)));
        result.add(listOf(anyType()));
        return result;
    }

    private List mergeSuperLists(List l1, List l2){
        List result = new ArrayList();
        for (Iterator it = l1.iterator(); it.hasNext(); ){
            List next = (List)it.next();
            for (Iterator jt = l2.iterator(); jt.hasNext(); ){
                List other = (List)jt.next();
                List merged = new ArrayList();
                merged.addAll(next);
                merged.addAll(other);
                result.add(merged);
            }
        }
        return result;
    }
    
    private Set handleContains(ParameterizedType pt){
        Set res = new HashSet();
        
        List typeArgs = pt.typeArguments();
        Iterator it = typeArgs.iterator();
        // always at least one
        List first = getSuperList((ReferenceType)it.next());
        while (it.hasNext()){
            List next = getSuperList((ReferenceType)it.next());
            first = mergeSuperLists(first, next);
        }
        for (Iterator jt = first.iterator(); jt.hasNext(); ){
            List l = (List)jt.next();
            ParameterizedType conPt = parameterizedType(pt.baseType());
            conPt.typeArguments(l);
            res.add(conPt);    
        }
        return res;
    }

    private ParameterizedType handleCapture(ParameterizedType pt){
        List capTypeArgs = new ArrayList();
        for (Iterator it = pt.typeArguments().iterator(); it.hasNext(); ){
            Type typeArg = (Type)it.next();
            if (typeArg instanceof AnySubType) {
                capTypeArgs.add(((AnySubType)typeArg).bound());
            }
            else if (typeArg instanceof AnySuperType){
                capTypeArgs.add(((AnySuperType)typeArg).upperBound());
            }
            else if (typeArg instanceof AnyType){
                capTypeArgs.add(((AnyType)typeArg).upperBound());
            }
            else {
                capTypeArgs.add(typeArg);
            }
        }
        ParameterizedType capPt = parameterizedType(pt.baseType());
        capPt.typeArguments(capTypeArgs);
        return capPt;
    }

    private Type getSubstitution(ParameterizedType orig, Type curr){
        Type sub = curr;
        if (curr instanceof IntersectionType){
            if (orig.typeVariables().contains(curr)){
                sub = (Type)orig.typeArguments().get(orig.typeVariables().indexOf(curr));
            }
        }
        else if (curr instanceof ParameterizedType){
            ParameterizedType nextP = (ParameterizedType)curr;
            for (Iterator it = nextP.typeArguments().iterator(); it.hasNext(); ){
                sub = getSubstitution(orig, (Type)it.next());
            }
        }
        return sub;
    }

    private Type getThetaSubstitution(ParameterizedType pt, Type superType){
        if (superType == null || !(superType instanceof ParameterizedType)) return null;
        ParameterizedType pSuper = (ParameterizedType)superType;
        List superArgs = new ArrayList();
        for (Iterator it = pSuper.typeArguments().iterator(); it.hasNext(); ){
            Type next = (Type)it.next();
            superArgs.add(getSubstitution(pt, next));
        }
        ParameterizedType sub = parameterizedType(pt.baseType());
        sub.typeArguments(superArgs);
        return sub;
    }
    
    private List directSupersOf(ReferenceType t){
        List results = new ArrayList();
        // direct superclass
        if (t.superType() != null){
            results.add(t.superType());
        }
        // direst superinterfaces
        for (Iterator it = t.interfaces().iterator(); it.hasNext(); ){
            ReferenceType next = (ReferenceType)it.next();
            results.add(next);
        }
        // Object, if t is an interface type with no direct superinterfaces.
        if (t.toClass().flags().isInterface() && t.interfaces().isEmpty()){
            results.add(Object());
        }
        // raw type if t is a param type
        if (t instanceof ParameterizedType){
            results.add(((ParameterizedType)t).baseType());
        
            // theta substitution
            Type theta = getThetaSubstitution((ParameterizedType)t, t.superType());
            if (theta != null){
                results.add(theta);
            }

            for (Iterator it = t.interfaces().iterator(); it.hasNext(); ){
                ReferenceType next = (ReferenceType)it.next();
                Type itheta = getThetaSubstitution((ParameterizedType)t, next);
                if (itheta != null){
                    results.add(itheta);
                }
            }

            // contains 
            results.addAll(handleContains((ParameterizedType)t));

            // capture
            results.add(handleCapture((ParameterizedType)t));

        }
        System.out.println("direct supers of: "+t+" are: "+results);
        return results;
    }
    
    public Set superTypesOf(ReferenceType t){
        LinkedList queue = new LinkedList();
        queue.add(t);
        Set result = new HashSet();
        result.add(t);
        while (!queue.isEmpty()){
            ReferenceType ref = (ReferenceType)queue.removeFirst();
            for (Iterator it = directSupersOf(ref).iterator(); it.hasNext(); ){
                ReferenceType next = (ReferenceType)it.next();
                if (!next.equals(ref) && !alreadyInResultSet(result, next)){
                    //System.out.println("queueing: "+next);
                    queue.add(next);
                    result.add(next);
                }
            }
        }
        return result;
    }

    public boolean alreadyInResultSet(Set results, Type t){
        if (t instanceof ParameterizedType){
            for (Iterator it = results.iterator(); it.hasNext(); ){
                Type next = (Type)it.next();
                if (next instanceof ParameterizedType){
                    if (equivalent(t, next)) return true;
                }
            }
            return false;
        }
        else {
            return results.contains(t);
        }
    }
}
