package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.ext.jl.types.*;
import java.util.*;

public class SyntheticType_c extends ReferenceType_c implements SyntheticType{

    protected List bounds;
    
    public SyntheticType_c(TypeSystem ts, List bounds){
        super(ts, null);
        this.bounds = bounds;
    }

    public List bounds(){
        return bounds;
    }
    
    public String translate(Resolver c){
        return "Synthetic Type";
    }

    public String toString(){
        return "Synthetic Type";
    }
    
    public boolean hasMethodImpl(MethodInstance mi){
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            if (((Type)it.next()).toReference().hasMethodImpl(mi)) return true;
        }
        return false;
    }

    public List methodsNamed(String name){
        List l = new ArrayList();
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            l.add(((Type)it.next()).toReference().methodsNamed(name));
        }
        return l;
    }

    public FieldInstance fieldNamed(String name){
        List l = new ArrayList();
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            Type next = (Type)it.next();
            if (next.toReference().fieldNamed(name) != null) return next.toReference().fieldNamed(name);
        }
        return null;
    }

    public List methods(){
        List l = new ArrayList();
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            l.addAll(((Type)it.next()).toReference().methods());
        }
        return l;
    }


    public List fields(){
        List l = new ArrayList();
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            l.addAll(((Type)it.next()).toReference().fields());
        }
        return l;
    }

    public List interfaces(){
        List l = new ArrayList();
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            l.addAll(((Type)it.next()).toReference().interfaces());
        }
        return l;
    }

    public Type superType(){
        return ((Type)bounds.get(0)).toReference().superType();
    }

    public ReferenceType toReference(){
        return this;
    }
    
    public boolean isReference(){
        return true;
    }


}
