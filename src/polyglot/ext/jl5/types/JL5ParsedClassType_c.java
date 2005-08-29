package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.ext.jl5.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.frontend.*;
import polyglot.ext.jl.types.*;

public class JL5ParsedClassType_c extends ParsedClassType_c implements JL5ParsedClassType{
    
    protected List enumConstants;
    protected List annotationElems;
   
    public JL5ParsedClassType_c( TypeSystem ts, LazyClassInitializer init, Source fromSource){
        super(ts, init, fromSource);
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
            //init.initEnumConstantDecls(this);
            freeInit();
        }
        return enumConstants;
    }
    
    public List annotationElems(){
        if (annotationElems == null){
            annotationElems = new TypedList(new LinkedList(), AnnotationElemInstance.class, false);
            freeInit();
        }
        return annotationElems;
    }

    protected boolean initialized(){
        return super.initialized() && this.enumConstants != null;
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
}
