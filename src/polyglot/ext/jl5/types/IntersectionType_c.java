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
    protected List restrictions;
    
    public IntersectionType_c(TypeSystem ts, Position pos, String id, List bounds){
        super(ts, pos);
        this.name = id;
        this.bounds = bounds;
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
        StringBuffer sb = new StringBuffer(name);
        return sb.toString();
    }

    public String toString(){
        return name;//+":"+bounds;
    }

    
    public boolean descendsFromImpl(Type ancestor) {
        // not sure about array types??
        if (ancestor instanceof ClassType){
            return isAnyBoundSubtype(ancestor);
        }
        /*else if (ancestor instanceof PrimitiveType){
            return isAnyBoundAutoUnboxingValid(ancestor);
        }*/
        return super.descendsFromImpl(ancestor);
    }

    private boolean isAnyBoundSubtype(Type ancestor){
        System.out.println("bounds: "+bounds);
        if (bounds == null || bounds.isEmpty()) return true;
        //return ((ClassType)ancestor).fullName().equals("java.lang.Object");
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            if (ts.isSubtype((Type)it.next(), ancestor)) return true;
        }
        return false;
    }

    /*private boolean isAnyBoundAutoUnboxingValid(Type ancestor){
        if (bounds == null || bounds.isEmpty()) return false;
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            if (ts.isSubtype((Type)it.next(), ancestor)) return true;
        }
        return false;
    }*/

    public void pushRestriction(TypeNode t){
        if (restrictions == null){
            // make TypedList with TypeNode
            restrictions = new ArrayList();
        }
        restrictions.add(t);
    }

    public void popRestriction(TypeNode t){
        System.out.println("removing restr: "+t);
        restrictions.remove(t);
    }

    public List restrictions(){
        return this.restrictions;
    }

    public TypeNode restriction(){
        if (restrictions == null || restrictions.isEmpty()) return null;
        TypeNode tn = (TypeNode)restrictions.get(restrictions.size()-1);    
        if (tn.type() instanceof IntersectionType){
            return ((IntersectionType)tn.type()).restriction();
        }
        return tn;
    }
}
