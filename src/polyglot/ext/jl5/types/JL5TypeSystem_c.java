package polyglot.ext.jl5.types;

import polyglot.frontend.*;
import polyglot.ext.jl.types.*;
import polyglot.types.*;
import polyglot.util.*;
import java.util.*;

public class JL5TypeSystem_c extends TypeSystem_c implements JL5TypeSystem {
    // TODO: implement new methods in JL5TypeSystem.
    // TODO: override methods as needed from TypeSystem_c.

    protected ClassType ENUM_;
    
    public ClassType Enum() { 
        if (ENUM_ != null) {
            return ENUM_;
        }
        else {
            return ENUM_ = load("java.lang.Enum");
        }
    }

    protected final Flags TOP_LEVEL_CLASS_FLAGS = JL5Flags.setEnumModifier(super.TOP_LEVEL_CLASS_FLAGS);

    protected final Flags MEMBER_CLASS_FLAGS = JL5Flags.setEnumModifier(super.MEMBER_CLASS_FLAGS);

    public void checkTopLevelClassFlags(Flags f) throws SemanticException {
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

    public void checkMemberClassFlags(Flags f) throws SemanticException {
        if (!f.clear(MEMBER_CLASS_FLAGS).equals(Flags.NONE)){
            throw new SemanticException("Cannot declare a member class with flag(s) " + f.clear(MEMBER_CLASS_FLAGS) + ".");
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
            // check enums elsewhere
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
    
    public EnumInstance enumInstance(Position pos, ClassType ct, Flags f, String name){
        assert_(ct);
        return new EnumInstance_c(this, pos, ct, f, name);
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
}
