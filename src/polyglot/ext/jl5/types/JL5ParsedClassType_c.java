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

import java.util.*;
import polyglot.ext.jl5.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.ast.*;
import polyglot.frontend.*;
import polyglot.ext.jl.types.*;

public class JL5ParsedClassType_c extends ParsedClassType_c implements JL5ParsedClassType, SignatureType{
    
    protected List enumConstants;
    // these are annotation elements in the annotation type
    protected List annotationElems;
  
    // these are annotations that have been declared on (applied to) the type
    protected List annotations;
    protected List typeVariables;
    protected List typeArguments;

    public JL5ParsedClassType_c( TypeSystem ts, LazyClassInitializer init, Source fromSource){
        super(ts, init, fromSource);
    }
    
    public void annotations(List annotations){
        this.annotations = annotations;
    }

    public List annotations(){
        return annotations;
    }
        
    
    public void addEnumConstant(EnumInstance ei){
        enumConstants().add(ei);
    }

    public void addAnnotationElem(AnnotationElemInstance ai){
        annotationElems().add(ai);
    }

    public List enumConstants(){
        if (enumConstants == null){
            enumConstants = new TypedList(new LinkedList(), EnumInstance.class, false);
            ((JL5LazyClassInitializer)init).initEnumConstants(this);
            freeInit();
        }
        return enumConstants;
    }
   
    protected void freeInit(){
        
    }
    
    public List annotationElems(){
        if (annotationElems == null){
            annotationElems = new TypedList(new LinkedList(), AnnotationElemInstance.class, false);
            ((JL5LazyClassInitializer)init).initAnnotations(this);
            freeInit();
        }
        return annotationElems;
    }

    protected boolean initialized(){
        return super.initialized() && this.enumConstants != null && this.annotationElems != null;
    }
    
    public EnumInstance enumConstantNamed(String name){
        for(Iterator it = enumConstants().iterator(); it.hasNext();){
            EnumInstance ei = (EnumInstance)it.next();
            if (ei.name().equals(name)){
                return ei;
            }
        }
        return null;
    }
    
    public AnnotationElemInstance annotationElemNamed(String name){
        for(Iterator it = annotationElems().iterator(); it.hasNext();){
            AnnotationElemInstance ai = (AnnotationElemInstance)it.next();
            if (ai.name().equals(name)){
                return ai;
            }
        }
        return null;
    }

    public void addMethod(MethodInstance mi){
        if (JL5Flags.isAnnotationModifier(flags())){
            //addAnnotationElem(ts.annotationElemInstance(mi.position(), this, mi.flags(), mi.type(), mi,name(), 
                        
        }
        super.addMethod(mi);
    }
    
    public boolean isImplicitCastValidImpl(Type toType){
        if (isAutoUnboxingValid(toType)) return true;
        if (toType instanceof IntersectionType){
            return isClassToIntersectionValid(toType);
        }
        return super.isImplicitCastValidImpl(toType);
    }

    private boolean isClassToIntersectionValid(Type toType){
        IntersectionType it = (IntersectionType)toType;
        if (it.bounds() == null || it.bounds().isEmpty()) return true;
        return ts.isImplicitCastValid(this, (Type)it.bounds().get(0));
    }
    
    private boolean isAutoUnboxingValid(Type toType){
        if (!toType.isPrimitive()) return false;
        if (this.fullName().equals("java.lang.Integer")) return ts.isImplicitCastValid(ts.Int(), toType);
        if (this.fullName().equals("java.lang.Boolean")) return ts.isImplicitCastValid(ts.Boolean(), toType);
        if (this.fullName().equals("java.lang.Byte")) return ts.isImplicitCastValid(ts.Byte(), toType);
        if (this.fullName().equals("java.lang.Short")) return ts.isImplicitCastValid(ts.Short(), toType);
        if (this.fullName().equals("java.lang.Character")) return ts.isImplicitCastValid(ts.Char(), toType);
        if (this.fullName().equals("java.lang.Long")) return ts.isImplicitCastValid(ts.Long(), toType);
        if (this.fullName().equals("java.lang.Float")) return ts.isImplicitCastValid(ts.Float(), toType);
        if (this.fullName().equals("java.lang.Double")) return ts.isImplicitCastValid(ts.Double(), toType);
        /*if (toType.isInt() && this.fullName().equals("java.lang.Integer")) return true;
        if (toType.isBoolean() && this.fullName().equals("java.lang.Boolean")) return true;
        if (toType.isByte() && this.fullName().equals("java.lang.Byte")) return true;
        if (toType.isShort() && this.fullName().equals("java.lang.Short")) return true;
        if (toType.isChar() && this.fullName().equals("java.lang.Character")) return true;
        if (toType.isLong() && this.fullName().equals("java.lang.Long")) return true;
        if (toType.isDouble() && this.fullName().equals("java.lang.Double")) return true;
        if (toType.isFloat() && this.fullName().equals("java.lang.Float")) return true;*/
        return false;
    }

    public boolean descendsFromImpl(Type ancestor){
        if (ancestor instanceof IntersectionType){
            return isSubtypeOfAllBounds(((IntersectionType)ancestor).bounds());
        }
        else if (ancestor instanceof ParameterizedType){
            // if there is ever a regular parsed class type being assigned 
            // to a parameterized type a warning should be generated
            // and then the base of the param type should be checked
            System.out.println("Warning: unchecked conversion");
            // this doesn't work
            return typeSystem().isSubtype(this, ((ParameterizedType)ancestor).baseType());
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
        else{
            return super.descendsFromImpl(ancestor);
        }
    }

    private boolean isSubtypeOfAllBounds(List bounds){
        if (bounds != null){
            for (Iterator it = bounds.iterator(); it.hasNext(); ){
                Object next = it.next();
                if (!typeSystem().isSubtype(this, (Type)next)) return false;
            }
        }
        return true;
    }

    public List typeVariables(){
        return typeVariables;
    }
    
    // this is used when coming from source files
    public void addTypeVariable(IntersectionType type){
        if (typeVariables == null){
            typeVariables = new TypedList(new LinkedList(), IntersectionType.class, false);
        }
        if (!typeVariables.contains(type)){
            typeVariables.add(type);
        }
    }

    // this is used when coming from class files
    public void typeVariables(List vars){
        typeVariables = vars;
    }
    
    public boolean hasTypeVariable(String name){
        if (typeVariables == null || typeVariables.isEmpty()) return false;
        for (Iterator it = typeVariables.iterator(); it.hasNext(); ){
            IntersectionType iType = (IntersectionType)it.next();
            if (iType.name().equals(name)) return true;
        }
        return false;
    }

    public IntersectionType getTypeVariable(String name){
        for (Iterator it = typeVariables.iterator(); it.hasNext(); ){
            IntersectionType iType = (IntersectionType)it.next();
            if (iType.name().equals(name)) return iType;
        }
        return null;
    }

    public boolean isGeneric(){
        if ((typeVariables != null) && !typeVariables.isEmpty()) return true;
        return false;
    }

    // this is only for debugging or something
    public String toString(){
        StringBuffer sb = new StringBuffer(super.toString());
        if ((typeVariables != null) && !typeVariables.isEmpty()){
            sb.append("<");
            sb.append(typeVariables);
            sb.append(">");
        }
        return sb.toString();
    }

    public LazyClassInitializer init(){
        return this.init;
    }

    /*public List typeArguments(){
        return typeArguments;
    }

    public void typeArguments(List args){
        this.typeArguments = args;
    }
   
    public boolean isParameterized(){
        if (typeArguments != null && !typeArguments.isEmpty()) return true;
        return false;
    }
    
    public String translate(Resolver c){
        StringBuffer sb = new StringBuffer(super.translate(c));
        if (isParameterized()){
            sb.append("<");
            for (Iterator it = typeArguments().iterator(); it.hasNext(); ){
                sb.append(((Type)it.next()).translate(c));
                if (it.hasNext()){
                    sb.append(", ");
                }
            }
            sb.append(">");
        }
        return sb.toString();
    }*/

    public boolean numericConversionValidImpl(long l){
        return numericConversionValid(new Long(l));
    }

    public boolean numericConversionValidImpl(Object value){
        if (value == null) return false;

        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (value instanceof Float){
            //if (ts.equals(this, ts.FloatWrapper())) return true;
            return false;
        }
        if (value instanceof Double){
            //if (ts.equals(this, ts.DoubleWrapper())) return true;
            return false;
        }

        long v;
        if (value instanceof Number){
            v = ((Number) value).longValue();
        }
        else if (value instanceof Character){
            v = ((Character) value).charValue();
        }
        else {
            return false;
        }

        if (ts.equals(this, ts.LongWrapper()) && value instanceof Long) return true;
        if (ts.equals(this, ts.IntegerWrapper()) && value instanceof Integer) return Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE;
        if (ts.equals(this, ts.CharacterWrapper()) && value instanceof Character) return Character.MIN_VALUE <= v && v <= Character.MAX_VALUE;
        if (ts.equals(this, ts.ShortWrapper()) && value instanceof Short) return Short.MIN_VALUE <= v && v <= Short.MAX_VALUE;
        if (ts.equals(this, ts.ByteWrapper()) && value instanceof Byte) return Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE;

        return false;
    }

    /*public boolean equalsImpl(TypeObject t){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (t instanceof PrimitiveType){ 
            if (this.isBoolean() && ((Type)t).isBoolean()) return true;
            if (this.isInt() && ((Type)t).isInt()) return true;
            if (this.isByte() && ((Type)t).isByte()) return true;
            if (this.isShort() && ((Type)t).isShort()) return true;
            if (this.isChar() && ((Type)t).isChar()) return true;
            if (this.isLong() && ((Type)t).isLong()) return true;
            if (this.isFloat() && ((Type)t).isFloat()) return true;
            if (this.isDouble() && ((Type)t).isDouble()) return true;
        }
        return super.equalsImpl(t);
    }*/

    public boolean equivalentImpl(TypeObject t){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (t instanceof PrimitiveType){ 
            if (this.isBoolean() && ((Type)t).isBoolean()) return true;
            if (this.isInt() && ((Type)t).isInt()) return true;
            if (this.isByte() && ((Type)t).isByte()) return true;
            if (this.isShort() && ((Type)t).isShort()) return true;
            if (this.isChar() && ((Type)t).isChar()) return true;
            if (this.isLong() && ((Type)t).isLong()) return true;
            if (this.isFloat() && ((Type)t).isFloat()) return true;
            if (this.isDouble() && ((Type)t).isDouble()) return true;
        }
        return false;
    }

    public boolean isNumeric(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.IntegerWrapper())) return true;
        if (this.equals(ts.ByteWrapper())) return true;
        if (this.equals(ts.ShortWrapper())) return true;
        if (this.equals(ts.CharacterWrapper())) return true;
        if (this.equals(ts.LongWrapper())) return true;
        if (this.equals(ts.FloatWrapper())) return true;
        if (this.equals(ts.DoubleWrapper())) return true;
        return false;
    }

    public boolean isByte(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.ByteWrapper())) return true;
        return false;
    }

    public boolean isShort(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.ShortWrapper())) return true;
        return false; 
    }

    public boolean isIntOrLess(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.IntegerWrapper())) return true;
        if (this.equals(ts.ByteWrapper())) return true;
        if (this.equals(ts.ShortWrapper())) return true;
        if (this.equals(ts.CharacterWrapper())) return true;
        return false; 
    }

    public boolean isInt(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.IntegerWrapper())) return true;
        return false;
    }

    public PrimitiveType toPrimitive() {
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.IntegerWrapper())) return typeSystem().Int();
        if (this.equals(ts.ByteWrapper())) return typeSystem().Byte();
        if (this.equals(ts.ShortWrapper())) return typeSystem().Short();
        if (this.equals(ts.CharacterWrapper())) return typeSystem().Char();
        if (this.equals(ts.LongWrapper())) return typeSystem().Long();
        if (this.equals(ts.FloatWrapper())) return typeSystem().Float();
        if (this.equals(ts.DoubleWrapper())) return typeSystem().Double();
        return super.toPrimitive();
    }

    public boolean isBoolean(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.BooleanWrapper())) return true;
        return false;
    }

    public boolean isChar(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.CharacterWrapper())) return true;
        return false;
    }

    public boolean isFloat(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.FloatWrapper())) return true;
        return false;
    }

    public boolean isLong(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.LongWrapper())) return true;
        return false;
    }
    
    public boolean isDouble(){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (this.equals(ts.DoubleWrapper())) return true;
        return false;
    }


    public boolean isCastValidImpl(Type toType){
        JL5TypeSystem ts = (JL5TypeSystem)typeSystem();
        if (isNumeric() || isBoolean()){
            if (isBoolean() && toType.isBoolean()) return true;
            if (isByte() && (toType.isByte() || toType.isShort() || toType.isInt() || toType.isLong() || toType.isFloat() || toType.isDouble())) return true;
            if (isShort() && (toType.isShort() || toType.isInt() || toType.isLong() || toType.isFloat() || toType.isDouble())) return true;
            if (isChar() && (toType.isChar() || toType.isInt() || toType.isLong() || toType.isFloat() || toType.isDouble())) return true;
            if (isInt() && (toType.isInt() || toType.isLong() || toType.isFloat() || toType.isDouble())) return true;
            if (isLong() && (toType.isLong() || toType.isFloat() || toType.isDouble())) return true;
            if (isFloat() && (toType.isFloat() || toType.isDouble())) return true;
            if (isDouble() && toType.isDouble()) return true;

            //if (toType.isPrimitive()) return ts.isCastValid(toPrimitive(), toType);
        }
        return super.isCastValidImpl(toType);
    }
    
    public String translate(Resolver c) {
        if (isTopLevel()) {
            if (package_() == null) {
                return name();
            }

            // Use the short name if it is unique.
            if (c != null) {
                try {
                    Named x = c.find(name());

                    if (ts.equals(this, x)) {
                        return name();
                    }
                }
                catch (SemanticException e) {
                }
            }

            return package_().translate(c) + "." + name();
        }
        else if (isMember()) {
            // Use only the short name if the outer class is anonymous.
            if (container().toClass().isAnonymous()) {
                return name();
            }

            // Use the short name if it is unique.
            if (c != null) {
                try {
                    Named x = c.find(name());

                    if (ts.equals(this, x) && !(container() instanceof ParameterizedType)) {
                        return name();
                    }
                }
                catch (SemanticException e) {
                }
            }

            return container().translate(c) + "." + name();
        }
        else if (isLocal()) {
            return name();
        }
        else {
            throw new InternalCompilerError("Cannot translate an anonymous class.");
        }
    }

    public String signature(){
        return "L"+fullName().replaceAll("\\.", "/")+";";
    }
}
