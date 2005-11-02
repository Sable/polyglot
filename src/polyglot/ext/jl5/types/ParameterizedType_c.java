package polyglot.ext.jl5.types;

import java.util.*;
import polyglot.types.*;
import polyglot.ext.jl.types.*;
import polyglot.util.*;
import polyglot.frontend.*;


public class ParameterizedType_c extends JL5ParsedClassType_c implements ParameterizedType {

    protected List typeArguments;
    protected JL5ParsedClassType baseType;
    
    public ParameterizedType_c(TypeSystem ts, LazyClassInitializer init, Source fromSource){//, List typeArgs){
        super(ts, init, fromSource);
        //this.rawType = raw;
        //this.typeArguments = typeArgs;
    }

    public ParameterizedType_c(JL5ParsedClassType t){
        super(t.typeSystem(), t.init(), t.fromSource());
        this.baseType = t;
        /*fromSource = t.fromSource();
        superType = t.superType();
        interfaces = t.interfaces();
        methods = t.methods();
        fields = t.fields();
        constructors = t.constructors();
        memberClasses = t.memberClasses();
        package_ = t.package_();
        flags = t.flags();
        kind = t.kind();
        name = t.name();
        outer = t.outer();
        inStaticContext = t.inStaticContext();
        enumConstants = t.enumConstants();
        annotationElems = t.annotationElems();
        annotations = t.annotations();
        typeVariables = t.typeVariables();*/
    }
  
    public Source fromSource() {
        return baseType.fromSource();
    }

    public Kind kind(){
        return baseType.kind();
    }

    public boolean inStaticContext(){
        return baseType.inStaticContext();
    }

    public ClassType outer(){
        return baseType.outer();
    }
    
    public String name(){
        return baseType.name();
    }

    public Type superType(){
        return baseType.superType();
    }
    
    public polyglot.types.Package package_(){
        return baseType.package_();
    }

    public Flags flags(){
        return baseType.flags();
    }

    public List constructors(){
        return baseType.constructors();
    }

    public List memberClasses(){
        return baseType.memberClasses();
    }
    
    public List methods(){
        return baseType.methods();
    }

    public List methods(String name, List args){
        return baseType.methods(name, args);
    }

    public List fields(){
        return baseType.fields();
    }

    public List interfaces(){
        return baseType.interfaces(); 
    }
    /*public ReferenceType toReference() {
        return this.baseType; 
    }

    public ClassType toClass(){
        return this.baseType;
    }*/
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public void typeArguments(List args){
        this.typeArguments = args;
    }

    public JL5ParsedClassType baseType(){
        return this.baseType;
    }

    public String translate(Resolver c){
        StringBuffer sb = new StringBuffer(baseType.translate(c));
        sb.append("<");
        for (Iterator it = typeArguments().iterator(); it.hasNext(); ){
            sb.append(((Type)it.next()).translate(c));
            if (it.hasNext()){
                sb.append(", ");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    public String toString(){
        StringBuffer sb = new StringBuffer(baseType.toString());
        sb.append("<");
        for (Iterator it = typeArguments().iterator(); it.hasNext(); ){
            sb.append(((Type)it.next()));
            if (it.hasNext()){
                sb.append(", ");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    public boolean descendsFromImpl(Type ancestor){
        if (!(ancestor instanceof ParameterizedType)){
            return ts.isSubtype(baseType, ancestor);
        }
        else {
            // first the base types have to have a subtype relationship
            if (!typeSystem().isSubtype(baseType(), ((ParameterizedType)ancestor).baseType())) return false;
            // if args match its okay 
            return argsDescend((ParameterizedType)ancestor);
            // if args are subtypes its not okay unless one is a type var
            // if args are unrelated its not okay
            // if wildcards
        }
    }

    private boolean argsDescend(ParameterizedType ancestor){
        for (int i = 0; i < ancestor.typeArguments().size(); i++){
            
            Type arg1 = (Type)ancestor.typeArguments().get(i);
            Type arg2 = (Type)typeArguments().get(i);
            IntersectionType cap1 = (IntersectionType)ancestor.typeVariables().get(i);
            IntersectionType cap2 = (IntersectionType)baseType().typeVariables().get(i);
            
            // if both are AnySubType then arg2 bound must be subtype 
            // of arg1 bound
            if (arg1 instanceof AnySubType){
                if (arg2 instanceof AnySubType){
                    if (!typeSystem().isSubtype(((AnySubType)arg2).bound(), ((AnySubType)arg1).bound())) return false;
                }
                else if (arg2 instanceof AnySuperType || arg2 instanceof AnyType){
                    if (!typeSystem().isSubtype(cap2, ((AnySubType)arg1).bound())) return false;
                }
                else if (arg2 instanceof IntersectionType){
                    // need to break out here or will recurse for ever
                    if (((IntersectionType)arg2).name().equals(((IntersectionType)((AnySubType)arg1).bound()).name())) return true;
                    //if (typeSystem().equals(arg2, ((AnySubType)arg1).bound())) return true;
                }
                // if only ancestor(arg1) is AnySubType then arg2 is not
                // wildcard must be subtype of bound of arg1
                else {
                    if (!typeSystem().isSubtype(arg2, ((AnySubType)arg1).bound())) return false;
                }
            }
            // if both are AnySuperType then arg1 bound must be a subtype
            // of arg2 bound
            else if (arg1 instanceof AnySuperType){
                if (arg2 instanceof AnySuperType){
                    if (!typeSystem().isSubtype(((AnySuperType)arg1).bound(), ((AnySuperType)arg2).bound())) return false;
                }
                // if only arg1 instanceof AnySuperType then arg1 bounds 
                // must be a subtype of arg2
                else {
                    if (!typeSystem().isSubtype(((AnySuperType)arg1).bound(), arg2)) return false;
                }
            }
            // if arg1 is ? then every arg2 is okay
            else if (arg1 instanceof AnyType) {
                if (arg2 instanceof AnySubType){
                    if (!typeSystem().isSubtype(cap2, cap1)) return false;    
                }
                else {
                    if (!typeSystem().isSubtype(arg2, cap1)) return false;   
                }
            }

            /*else if (arg1 instanceof IntersectionType){
                if (arg2 instanceof IntersectionType){
                // need to get infered type here (ie arg2 should never be
                // an intersection type
                    if (((IntersectionType)arg1).name().equals(((IntersectionType)arg2).name())) return true;
                    if (!typeSystem().isSubtype(arg1, arg2)) return false;
                }*/
            else if (arg1 instanceof ParameterizedType && arg2 instanceof ParameterizedType){
                    //if (arg1.equals(arg2)) return true;
                    if (!typeSystem().isSubtype(arg1, arg2)) return false;
            }
            else {
                if (!typeSystem().equals(arg1, arg2)) return false;
            }
        }
        return true;
    }

    private boolean argsEqual(ParameterizedType ancestor){
        for (int i = 0; i < ancestor.typeArguments().size(); i++){
            
            Type arg1 = (Type)ancestor.typeArguments().get(i);
            Type arg2 = (Type)typeArguments().get(i);
            IntersectionType cap1 = (IntersectionType)ancestor.typeVariables().get(i);
            IntersectionType cap2 = (IntersectionType)baseType().typeVariables().get(i);
            // if both are AnySubType then arg2 bound must be subtype 
            // of arg1 bound
            if (arg1 instanceof AnySubType){
                if (arg2 instanceof AnySubType){
                    if (!typeSystem().equals(((AnySubType)arg2).bound(), ((AnySubType)arg1).bound())) return false;
                }
                else if (arg2 instanceof AnySuperType){
                    if (!typeSystem().equals(((AnySubType)arg1).bound(), ((AnySuperType)arg2).bound())) return false;
                }
                else if (arg2 instanceof IntersectionType){
                    // need to break out here or will recurse for ever
                    if (((IntersectionType)arg2).name().equals(((IntersectionType)((AnySubType)arg1).bound()).name())) return true;
                }
                // if only ancestor(arg1) is AnySubType then arg2 is not
                // wildcard must be subtype of bound of arg1
                else {
                    if (!typeSystem().equals(arg2, arg1)) return false;
                }
            }
            // if both are AnySuperType then arg1 bound must be a subtype
            // of arg2 bound
            else if (arg1 instanceof AnySuperType){
                if (arg2 instanceof AnySuperType){
                    if (!typeSystem().equals(((AnySuperType)arg1).bound(), ((AnySuperType)arg2).bound())) return false;
                }
                // if only arg1 instanceof AnySuperType then arg1 bounds 
                // must be a subtype of arg2
                else {
                    if (!typeSystem().equals(arg1, arg2)) return false;
                }
            }
            else if (arg1 instanceof AnyType){
                if (arg2 instanceof AnyType){
                    if (!typeSystem().equals(((AnyType)arg1).upperBound(), ((AnyType)arg2).upperBound())) return false;
                }
                else {
                    if (!typeSystem().equals(arg1, arg2)) return false;
                }
            }
            else if (arg1 instanceof ParameterizedType && arg2 instanceof ParameterizedType){
                    //if (arg1.equals(arg2)) return true;
                    if (!typeSystem().equals(arg1, arg2)) return false;
            }
            else if (arg1 instanceof IntersectionType && arg2 instanceof IntersectionType){
                if (!typeSystem().equals(arg1, arg2) && !((JL5TypeSystem)typeSystem()).isEquivalent(arg1, arg2)) return false;
            }
            else {
                if (!typeSystem().equals(arg1, arg2)) return false;
            }
        }
        return true;
    }

    public boolean equalsImpl(TypeObject t){
        if (!(t instanceof ParameterizedType)) return super.equalsImpl(t);
        return argsEqual((ParameterizedType)t);
    }

 
    public boolean isGeneric(){
        return baseType.isGeneric();
    }

    public List typeVariables(){
        return baseType.typeVariables();
    }

    public boolean comprisedOfIntersectionType(IntersectionType iType){
        for (Iterator it = typeArguments().iterator(); it.hasNext(); ){
            Type next = (Type)it.next();
            if (next instanceof IntersectionType && ts.equals(next, iType)) return true;
            if (next instanceof ParameterizedType && ((ParameterizedType)next).comprisedOfIntersectionType(iType)) return true;
        }
        return false;
    }
    
    public Type convertToInferred(List typeVars, List inferredTypes){
        List newBounds = new ArrayList();
        for (Iterator it = typeArguments().iterator(); it.hasNext(); ){
            Type next = (Type) it.next();
            if (next instanceof IntersectionType){
                newBounds.add(inferredTypes.get(typeVars.indexOf(next)));
            }
            else if (next instanceof ParameterizedType){
                newBounds.add(((ParameterizedType)next).convertToInferred(typeVars, inferredTypes));
            }
            /*else if (next instanceof AnySubType){
                newBounds.add(((AnySubType)next).convertToInferred(typeVars, inferredTypes));
            }*/
            else {
                newBounds.add(next);
            }
        }
        ParameterizedType converted = ((JL5TypeSystem)typeSystem()).parameterizedType(this.baseType());
        converted.typeArguments(newBounds);
        return converted;
    }
}
