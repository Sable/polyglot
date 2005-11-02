package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import java.util.*;

public class AnySubType_c extends ReferenceType_c implements AnySubType{

    protected Type bound;
    
    public AnySubType_c(TypeSystem ts, Type bound){
        super(ts);
        this.bound = bound;
    }
    
    public Type bound(){
        return bound;
    }

    public String translate(Resolver c){
        return "? extends "+bound.translate(c);
    }

    public String toString(){
        return "? extends "+bound.toString();
    }

    public boolean hasMethodImpl(MethodInstance mi){
        return bound.toReference().hasMethodImpl(mi);
    }

    public List methods(String name, List args){
        return bound.toReference().methods(name, args);
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

    public Type convertToInferred(List typeVars, List inferredTypes){
        if (bound instanceof IntersectionType){
            return ((JL5TypeSystem)typeSystem()).anySubType((Type)inferredTypes.get(typeVars.indexOf(bound)));
        } 
        else if (bound instanceof ParameterizedType){
            return ((JL5TypeSystem)typeSystem()).anySubType(((ParameterizedType)bound).convertToInferred(typeVars, inferredTypes));
        }
        else {
            return this;
        }
    }

    public boolean equalsImpl(TypeObject ancestor){
        if (ancestor instanceof AnySuperType){
            return ts.equals(bound(), ((AnySuperType)ancestor).bound());
        }
        return super.equalsImpl(ancestor);
    }
}
