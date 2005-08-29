package polyglot.ext.jl5.types;

import java.lang.reflect.Modifier;

import polyglot.frontend.*;
import polyglot.ext.jl.types.*;
import polyglot.ext.jl5.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import java.util.*;

public class JL5TypeSystem_c extends TypeSystem_c implements JL5TypeSystem {
    // TODO: implement new methods in JL5TypeSystem.
    // TODO: override methods as needed from TypeSystem_c.

    protected ClassType ENUM_;
    protected ClassType ANNOTATION_;
    
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

    public ParsedClassType createClassType(LazyClassInitializer init, Source fromSource){
        return new JL5ParsedClassType_c(this, init, fromSource);
    }

    public void checkClassConformance(ClassType ct) throws SemanticException {
    
        if (JL5Flags.isEnumModifier(ct.flags())){
            // check enums elsewhere - have to do something special with
            // abstract methods and anon enum element bodies
            return;
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
        EnumInstance ei = ((JL5ParsedClassType)container).enumConstantNamed(name);

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
        
}
