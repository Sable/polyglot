package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import java.util.*;

public class AnySubType_c extends AnyType_c implements AnySubType{

    protected Type bound;
    
    public AnySubType_c(TypeSystem ts, Type bound){
        super(ts);
        this.bound = bound;
    }
    
    public Type bound(){
        return bound;
    }

    public String translate(Resolver c){
        return super.translate(c) + " extends "+bound.translate(c);
    }

    public String toString(){
        return super.toString()+" extends "+bound.toString();
    }

    public boolean hasMethodImpl(MethodInstance mi){
        return bound.toReference().hasMethodImpl(mi);
    }

    public boolean hasMethod(MethodInstance mi){
        return bound.toReference().hasMethod(mi);
    }

    public List methods(String name, List args){
        return bound.toReference().methods();
    }

    public List methodsNamed(String name){
        return bound.toReference().methodsNamed(name);
    }

    public FieldInstance fieldNamed(String name){
        return bound.toReference().fieldNamed(name);
    }

    public List methods(){
        return bound.toReference().methods();
    }

    public List fields(){
        return bound.toReference().fields();
    }

    public List interfaces(){
        return bound.toReference().interfaces();
    }

    public Type superType(){
        return bound.toReference().superType();
    }

    public ReferenceType toReference(){
        return this;
    }
    
    public boolean isReference(){
        return true;
    }
}
