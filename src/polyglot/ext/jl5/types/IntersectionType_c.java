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

    public boolean isCastValidImpl(Type toType){
        return ts.isCastValid(superType(), toType);
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
        if (bounds == null || bounds.isEmpty()) return true;
        //return ((ClassType)ancestor).fullName().equals("java.lang.Object");
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            if (ts.isSubtype((Type)it.next(), ancestor)) return true;
        }
        return false;
    }

    public boolean equalsImpl(TypeObject other){
        //System.out.println("considering equals for intersection type");
        //System.out.println("other: "+other.getClass());
        if (!(other instanceof IntersectionType)) return super.equalsImpl(other);
        IntersectionType arg2 = (IntersectionType)other;
        if (this.name.equals(arg2.name()) && allBoundsEqual(arg2)) return true;
        return false;
    }

    private boolean allBoundsEqual(IntersectionType arg2){
        //System.out.println("checking bounds: "+bounds()+" arg2 bounds: "+arg2.bounds());
        if ((bounds == null || bounds.isEmpty()) && (arg2.bounds() == null || arg2.bounds().isEmpty()) ) return true;
        Iterator it = bounds.iterator();
        Iterator jt = arg2.bounds().iterator();
        while (it.hasNext() && jt.hasNext()){
            if (!ts.equals((Type)it.next(), (Type)jt.next())) {
                //System.out.println("found unequal bound");
                return false;
            }
        }
        if (it.hasNext() || jt.hasNext()) return false;
        return true;
    }
    

    public boolean isEquivalent(TypeObject arg2){
        return typeSystem().isSubtype(rawType(), (Type)arg2) || typeSystem().isSubtype((Type)arg2, rawType());
    }

    public Type rawType(){
        if (bounds == null || bounds.isEmpty()) return typeSystem().Object();
        return (Type)bounds.get(0);
    }
}
