package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ast.*;
import java.util.*;
import polyglot.util.*;

public class IntersectionType_c extends ClassType_c implements IntersectionType {
    protected String name;
    protected polyglot.types.Package package_; // package is same as reified type
    protected List bounds;
    protected Flags flags;
    
    public IntersectionType_c(TypeSystem ts){
        super(ts);
        bounds = new TypedList(new LinkedList(), ClassType.class, false);
    }

    public List bounds(){
        return bounds;
    }

    public void addBound(ClassType bound){
        bounds.add(bound);
    }

    public void bounds(List b){
        this.bounds = b;
    }
    
    public Kind kind(){
        return INTERSECTION;
    }

    public ClassType outer(){
        return null;
    }

    public String name(){
        return name;
    }

    public void name(String name){
        this.name = name;
    }
    
    public polyglot.types.Package package_(){
        return package_;    
    }

    public Flags flags(){
        return flags;
    }

    public List constructors(){
        List l = new TypedList(new LinkedList(), ConstructorInstance.class, false);
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            ClassType ct = (ClassType)it.next();
            l.addAll(ct.constructors());
        }
        return l;
    }

    public List memberClasses(){
        List l = new TypedList(new LinkedList(), Type.class, false);
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            ClassType ct = (ClassType)it.next();
            l.addAll(ct.memberClasses());
        }
        return l;
    }

    public List methods(){
        List l = new TypedList(new LinkedList(), MethodInstance.class, false);
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            ClassType ct = (ClassType)it.next();
            l.addAll(ct.methods());
        }
        return l;
    }

    public List fields(){
        List l = new TypedList(new LinkedList(), FieldInstance.class, false);
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            ClassType ct = (ClassType)it.next();
            l.addAll(ct.fields());
        }
        return l;
    }
    
    public List interfaces(){
        List l = new TypedList(new LinkedList(), Type.class, false);
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            ClassType ct = (ClassType)it.next();
            l.addAll(ct.interfaces());
        }
        return l;
    }

    public Type superType(){
        if (bounds.isEmpty()) return ts.Object();
        return ((ClassType)bounds.get(0)).superType();
    }

    public boolean inStaticContext(){
        return false; // not sure
    }

    public String translate(Resolver c){
        return name;
    }
}
