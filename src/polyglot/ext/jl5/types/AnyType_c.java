package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import java.util.*;

public class AnyType_c extends ReferenceType_c implements AnyType{

    protected Type upperBound;
    
    public AnyType_c(TypeSystem ts){
        super(ts, null);
    }

    public String translate(Resolver c){
        return "?";
    }

    public String toString(){
        return "?";
    }

    public Type upperBound(){
        if (upperBound == null) return typeSystem().Object();
        return upperBound;
    }
    
    public void upperBound(Type t){
        upperBound = t;
    }
    
    public boolean hasMethodImpl(MethodInstance mi){
        return upperBound().toReference().hasMethodImpl(mi);
    }

    public List methodsNamed(String name){
        return upperBound().toReference().methodsNamed(name);
    }

    public FieldInstance fieldNamed(String name){
        return upperBound().toReference().fieldNamed(name);
    }

    public List methods(String name, List args){
        return upperBound().toReference().methods(name, args);
    }

    public List methods(){
        System.out.println("upper bound: "+upperBound());
        return upperBound().toReference().methods();
    }

    public List fields(){
        return upperBound().toReference().fields();
    }

    public List interfaces(){
        return upperBound().toReference().interfaces();
    }

    public Type superType(){
        return upperBound().toReference().superType();
    }

    public ReferenceType toReference(){
        return this;
    }
    
    public boolean isReference(){
        return true;
    }
}
