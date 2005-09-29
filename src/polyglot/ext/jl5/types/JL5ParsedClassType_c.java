package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.ext.jl5.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.ext.jl.types.*;

public class JL5ParsedClassType_c extends ParsedClassType_c implements JL5ParsedClassType{
    
    protected List enumConstants;
    // these are annotation elements in the annotation type
    protected List annotationElems;
  
    // these are annotations that have been declared on (applied to) the type
    protected List annotations;
    
    public JL5ParsedClassType_c( TypeSystem ts, LazyClassInitializer init, Source fromSource){
        super(ts, init, fromSource);
    }
    
    public void annotations(List annotations){
        this.annotations = annotations;
    }

    public List annotations(){
        return annotations;
    }
        
    
    public void addEnumConstant(EnumInstance ei){
        enumConstants().add(ei);
    }

    public void addAnnotationElem(AnnotationElemInstance ai){
        annotationElems().add(ai);
    }

    public List enumConstants(){
        if (enumConstants == null){
            enumConstants = new TypedList(new LinkedList(), EnumInstance.class, false);
            ((JL5LazyClassInitializer)init).initEnumConstants(this);
            freeInit();
        }
        return enumConstants;
    }
    
    public List annotationElems(){
        if (annotationElems == null){
            annotationElems = new TypedList(new LinkedList(), AnnotationElemInstance.class, false);
            ((JL5LazyClassInitializer)init).initAnnotations(this);
            freeInit();
        }
        return annotationElems;
    }

    protected boolean initialized(){
        return super.initialized() && this.enumConstants != null && this.annotationElems != null;
    }
    
    public EnumInstance enumConstantNamed(String name){
        for(Iterator it = enumConstants().iterator(); it.hasNext();){
            EnumInstance ei = (EnumInstance)it.next();
            if (ei.name().equals(name)){
                return ei;
            }
        }
        return null;
    }
    
    public AnnotationElemInstance annotationElemNamed(String name){
        for(Iterator it = annotationElems().iterator(); it.hasNext();){
            AnnotationElemInstance ai = (AnnotationElemInstance)it.next();
            if (ai.name().equals(name)){
                return ai;
            }
        }
        return null;
    }

    public void addMethod(MethodInstance mi){
        if (JL5Flags.isAnnotationModifier(flags())){
            //addAnnotationElem(ts.annotationElemInstance(mi.position(), this, mi.flags(), mi.type(), mi,name(), 
                        
        }
        super.addMethod(mi);
    }
    
    public boolean isImplicitCastValidImpl(Type toType){
        if (isAutoUnboxingValid(toType)) return true;
        if (toType instanceof IntersectionType){
            return isClassToIntersectionValid(toType);
        }
        return super.isImplicitCastValidImpl(toType);
    }

    private boolean isClassToIntersectionValid(Type toType){
        IntersectionType it = (IntersectionType)toType;
        if (it.bounds() == null || it.bounds().isEmpty()) return true;
        return ts.isImplicitCastValid(this, (Type)it.bounds().get(0));
    }
    
    private boolean isAutoUnboxingValid(Type toType){
        if (!toType.isPrimitive()) return false;
        if (toType.isInt() && this.fullName().equals("java.lang.Integer")) return true;
        if (toType.isBoolean() && this.fullName().equals("java.lang.Boolean")) return true;
        if (toType.isByte() && this.fullName().equals("java.lang.Byte")) return true;
        if (toType.isShort() && this.fullName().equals("java.lang.Short")) return true;
        if (toType.isChar() && this.fullName().equals("java.lang.Character")) return true;
        if (toType.isLong() && this.fullName().equals("java.lang.Long")) return true;
        if (toType.isDouble() && this.fullName().equals("java.lang.Double")) return true;
        if (toType.isFloat() && this.fullName().equals("java.lang.Float")) return true;
        return false;
    }

    public boolean descendsFromImpl(Type ancestor){
        if (ancestor instanceof IntersectionType){
            return isSubtypeOfAllBounds(((IntersectionType)ancestor).bounds());
        }
        else{
            return super.descendsFromImpl(ancestor);
        }
    }

    private boolean isSubtypeOfAllBounds(List bounds){
        if (bounds == null || bounds.isEmpty()) return true;
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            Object next = it.next();
            if (!typeSystem().isSubtype(this, (Type)next)) return false;
        }
        return true;
    }
}

