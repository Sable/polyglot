package polyglot.ext.jl5.types;

import polyglot.types.*;
import java.util.*;
import polyglot.ext.jl5.ast.*;
import polyglot.util.*;

public class FlagAnnotations {

    protected Flags classicFlags;
    protected List annotations;

    public FlagAnnotations(){
    }

    public Flags classicFlags(){
        if (classicFlags == null){
            classicFlags = Flags.NONE;
        }
        return classicFlags;
    }

    public FlagAnnotations classicFlags(Flags flags){
        if (this.classicFlags != null){
            this.classicFlags = this.classicFlags.set(flags);
        }
        else {
            this.classicFlags = flags;
        }
        return this;
    }

    public FlagAnnotations annotations(List annotations){
        this.annotations = annotations;
        return this;
    }
    
    public List annotations(){
        return annotations;
    }
    
    public FlagAnnotations addAnnotation(Object o){
        if (annotations == null){
            annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        annotations.add((AnnotationElem)o);
        return this;
    }
}
