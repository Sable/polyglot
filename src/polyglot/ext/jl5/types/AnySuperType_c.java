package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import java.util.*;

public class AnySuperType_c extends ReferenceType_c implements AnySuperType{

    protected Type bound;
    protected Type upperBound;
    
    public AnySuperType_c(TypeSystem ts, Type bound){
        super(ts);
        this.bound = bound;
    }

    public Type bound(){
        return bound;
    }
    
    public Type upperBound(){
        if (upperBound == null) return typeSystem().Object();
        return upperBound;
    }

    public void upperBound(Type t){
        upperBound = t;
    }
    
    public String translate(Resolver c){
        return "? super "+bound.translate(c);
    }

    public String toString(){
        return "? super "+bound.toString();
    }
    
    public boolean hasMethodImpl(MethodInstance mi){
        return upperBound().toReference().hasMethodImpl(mi);
    }

    public List methods(String name, List args){
        return upperBound().toReference().methods(name, args);
    }

    public List methodsNamed(String name){
        return upperBound().toReference().methodsNamed(name);
    }

    public FieldInstance fieldNamed(String name){
        return upperBound().toReference().fieldNamed(name);
    }

    public List methods(){
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

    public boolean isClass(){
        return true;
    }
    
    public boolean equalsImpl(TypeObject ancestor){
        if (ancestor instanceof AnySubType){
            return ts.equals(bound(), ((AnySubType)ancestor).bound());
        }
        return super.equalsImpl(ancestor);
    }

    public boolean descendsFromImpl(Type ancestor){
        if (ancestor instanceof IntersectionType){
            if (typeSystem().equals(bound(), ancestor)) return true;
        }
        return false;
    }
}
