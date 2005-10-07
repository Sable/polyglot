package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.types.*;
import polyglot.ext.jl.types.*;
import polyglot.util.*;
import polyglot.frontend.*;


public class ParameterizedType_c extends JL5ParsedClassType_c implements ParameterizedType {

    protected List typeArguments;
    //protected Type rawType;
    
    public ParameterizedType_c(TypeSystem ts, LazyClassInitializer init, Source fromSource){//, List typeArgs){
        super(ts, init, fromSource);
        //this.rawType = raw;
        //this.typeArguments = typeArgs;
    }

    public ParameterizedType_c(JL5ParsedClassType t){
        super(t.typeSystem(), t.init(), t.fromSource());
        init = t.init();
        fromSource = t.fromSource();
        superType = t.superType();
        interfaces = t.interfaces();
        methods = t.methods();
        fields = t.fields();
        constructors = t.constructors();
        memberClasses = t.memberClasses();
        package_ = t.package_();
        flags = t.flags();
        kind = t.kind();
        name = t.name();
        outer = t.outer();
        inStaticContext = t.inStaticContext();
        enumConstants = t.enumConstants();
        annotationElems = t.annotationElems();
        annotations = t.annotations();
        typeVariables = t.typeVariables();
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public void typeArguments(List args){
        this.typeArguments = args;
    }

    /*public Type rawType(){
        return rawType;
    }

    public String translate(Resolver c){
        return rawType.translate(c);
    }

    public String toString(){
        return rawType.toString();
    }*/
}
