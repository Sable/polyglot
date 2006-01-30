/* Copyright (C) 2006 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ast.*;
import java.util.*;
import polyglot.util.*;

public class IntersectionType_c extends ClassType_c implements IntersectionType , SignatureType{
    protected String name;
    protected polyglot.types.Package package_; // package is same as reified type
    protected List bounds;
    protected Flags flags;
    protected List restrictions;
    
    public IntersectionType_c(TypeSystem ts, Position pos, String id, List bounds){
        super(ts, pos);
        this.name = id;
        this.bounds = bounds;
        flags = Flags.NONE;
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
    
    public List methods(String name, List args){
        List l = new TypedList(new LinkedList(), MethodInstance.class, false);
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            ClassType ct = (ClassType)it.next();
            l.addAll(ct.methods(name, args));
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

    public Type upperBound(){
        if (bounds == null || bounds.isEmpty()) return typeSystem().Object();
        if (bounds.size() == 1) return (Type)bounds.get(0);
        return ((JL5TypeSystem)typeSystem()).syntheticType(bounds);
    }

    public boolean isCastValidImpl(Type toType){
        return ts.isCastValid(superType(), toType);
    }
    
    public boolean descendsFromImpl(Type ancestor) {
        // not sure about array types??
        if (ancestor instanceof ClassType || ancestor instanceof ParameterizedType){
            return isAnyBoundSubtype(ancestor);
        }
        else if (ancestor instanceof AnySuperType){
            return (typeSystem().isSubtype(((AnySuperType)ancestor).bound(), this) || typeSystem().isSubtype(this, ((AnySuperType)ancestor).bound())) && typeSystem().isSubtype(this, ((AnySuperType)ancestor).upperBound());
        }
        else if (ancestor instanceof AnySubType){
            return typeSystem().isSubtype(this, ((AnySubType)ancestor).bound());
        }
        else if (ancestor instanceof AnyType){
            return typeSystem().isSubtype(this, ((AnyType)ancestor).upperBound());
        }
        else { 
        /*else if (ancestor instanceof PrimitiveType){
            return isAnyBoundAutoUnboxingValid(ancestor);
        }*/
            return super.descendsFromImpl(ancestor);
        }
    }

    private boolean isAnyBoundSubtype(Type ancestor){
        if (bounds == null || bounds.isEmpty()) return true;
        //return ((ClassType)ancestor).fullName().equals("java.lang.Object");
        for (Iterator it = bounds.iterator(); it.hasNext(); ){
            Type next = (Type)it.next();
            if (ts.isSubtype(next, ancestor)) return true;
        }
        return false;
    }

    /*public boolean equalsImpl(TypeObject other){
        if (!(other instanceof IntersectionType)) return super.equalsImpl(other);
        IntersectionType arg2 = (IntersectionType)other;
        if (this.name.equals(arg2.name())) return true;// && allBoundsEqual(arg2)) return true;
        return false;
    }*/
    
    public boolean equivalentImpl(TypeObject other){
        if (!(other instanceof IntersectionType)) return super.equalsImpl(other);
        IntersectionType arg2 = (IntersectionType)other;
        if (this.name.equals(arg2.name())) return true;// && allBoundsEqual(arg2)) return true;
        return false;
    }


    private boolean allBoundsEqual(IntersectionType arg2){
        if ((bounds == null || bounds.isEmpty()) && (arg2.bounds() == null || arg2.bounds().isEmpty()) ) return true;
        Iterator it = bounds.iterator();
        Iterator jt = arg2.bounds().iterator();
        while (it.hasNext() && jt.hasNext()){
            /*Type t1 = (type)it.next();
            Type t2 = (type)jt.next();*/
            if (!ts.equals((Type)it.next(), (Type)jt.next())) {
                return false;
            }
        }
        if (it.hasNext() || jt.hasNext()) return false;
        return true;
    }
    

    public boolean isEquivalent(TypeObject arg2){
        if (arg2 instanceof IntersectionType){
            if (this.erasureType() instanceof ParameterizedType && ((IntersectionType)arg2).erasureType() instanceof ParameterizedType){
                return typeSystem().equals(((ParameterizedType)this.erasureType()).baseType(), ((ParameterizedType)((IntersectionType)arg2).erasureType()).baseType());
            }
            else {
                return typeSystem().equals(this.erasureType(), ((IntersectionType)arg2).erasureType());
            }
        }
        return false;
    }

    public Type erasureType(){
        if (bounds == null || bounds.isEmpty()) return typeSystem().Object();
        return (Type)bounds.get(0);
    }

    public ClassType toClass(){
        return this;
    }

    public String signature(){
        return "T"+name+";";
    }
}
