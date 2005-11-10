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
        }
        super.checkClassConformance(ct); 
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
    
    public EnumInstance enumInstance(Position pos, ClassType ct, Flags f, String name){
        assert_(ct);
        return new EnumInstance_c(this, pos, ct, f, name);
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

    public boolean equals(TypeObject arg1, TypeObject arg2){
        if (arg1 instanceof ParameterizedType) return ((ParameterizedType)arg1).equalsImpl(arg2);
        if (arg1 instanceof IntersectionType) return ((IntersectionType)arg1).equalsImpl(arg2);
        if (arg1 instanceof AnySubType) return ((AnySubType)arg1).equalsImpl(arg2);
        if (arg1 instanceof AnySuperType) return ((AnySuperType)arg1).equalsImpl(arg2);
        return super.equals(arg1, arg2);
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

    protected ArrayType arrayType(Position pos, Type type){
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

}
