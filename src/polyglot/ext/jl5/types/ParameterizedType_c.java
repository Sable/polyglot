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
        //System.out.println("descends from in param: "+this+" ances: "+ancestor+" is a: "+ancestor.getClass());
        if (!(ancestor instanceof ParameterizedType)){
            return ts.isSubtype(baseType, ancestor);
        }
        else {
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
            
            
            //System.out.println("arg1: "+arg1+" is a: "+arg1.getClass());
            //System.out.println("arg2: "+arg2+" is a: "+arg2.getClass());
            // if both are AnySubType then arg2 bound must be subtype 
            // of arg1 bound
            if (arg1 instanceof AnySubType && arg2 instanceof AnySubType){
                if (!typeSystem().isSubtype(((AnySubType)arg2).bound(), ((AnySubType)arg1).bound())) return false;
            }
            // if only ancestor(arg1) is AnySubType then arg2 must be subtype of
            // bound of arg1
            else if (arg1 instanceof AnySubType) {
                //System.out.println("sub: "+typeSystem().isSubtype(arg2, ((AnySubType)arg1).bound()));
                if (!typeSystem().isSubtype(arg2, ((AnySubType)arg1).bound())) return false;
            }
            // if both are AnySuperType then arg1 bound must be a subtype
            // of arg2 bound
            if (arg1 instanceof AnySuperType && arg2 instanceof AnySuperType){
                if (!typeSystem().isSubtype(((AnySuperType)arg1).bound(), ((AnySuperType)arg2).bound())) return false;
            }
            // if only arg1 instanceof AnySuperType then arg1 bounds 
            // must be a subtype of arg2
            else if (arg1 instanceof AnySuperType){
                if (!typeSystem().isSubtype(((AnySuperType)arg1).bound(), arg2)) return false;
            }
            
            // if arg1 is ? then every arg2 is okay
            if (arg1 instanceof AnyType) continue;

            if (arg1 instanceof IntersectionType && arg2 instanceof IntersectionType){
                // need to get infered type here (ie arg2 should never be
                // an intersection type
                if (!typeSystem().isSubtype(arg1, arg2)) return false;
            }
            else if (arg1 instanceof ParameterizedType && arg2 instanceof ParameterizedType){
                if (!typeSystem().isSubtype(arg1, arg2)) return false;
            }
            else {
                //System.out.println("args equals: "+typeSystem().equals(arg1, arg2));
                if (!typeSystem().equals(arg1, arg2)) return false;
            }
        }
        return true;
    }

    public boolean equalsImpl(TypeObject t){
        if (!(t instanceof ParameterizedType)) return super.equalsImpl(t);
        return argsDescend((ParameterizedType)t);
    }

 
    public boolean isGeneric(){
        return baseType.isGeneric();
    }

    public List typeVariables(){
        return baseType.typeVariables();
    }
}
