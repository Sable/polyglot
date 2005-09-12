package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.ext.jl5.ast.*;
import polyglot.util.*;
import polyglot.types.*;
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
}

