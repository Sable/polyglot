package polyglot.ext.jl5.types;

import polyglot.types.*;
import polyglot.ext.jl.types.*;
import polyglot.util.*;
import java.util.*;

public class JL5MethodInstance_c extends MethodInstance_c implements JL5MethodInstance{
    
    protected boolean compilerGenerated;
    protected List typeVariables;
    
    public JL5MethodInstance_c(TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type returnType, String name, List formals, List excTypes){
        super(ts, pos, container, flags, returnType, name, formals, excTypes);
    }

    /*public JL5MethodInstance_c(TypeSystem ts, Position pos, ReferenceType container, Flags flags, Type returnType, String name, List formals, List excTypes, List typeVariables){
        super(ts, pos, container, flags, returnType, name, formals, excTypes);
        this.typeVariables = typeVariables;
    }*/
    
    
    public boolean isCompilerGenerated(){
        return compilerGenerated;
    }
        
    public JL5MethodInstance setCompilerGenerated(boolean val){
        if (compilerGenerated != val) {
            JL5MethodInstance_c mi = (JL5MethodInstance_c)copy();
            mi.compilerGenerated = val;
        
            return mi;
        }
        return this;
    }

    public List typeVariables(){
        return typeVariables;
    }

    public void addTypeVariable(IntersectionType type){
        if (typeVariables == null){
            typeVariables = new TypedList(new LinkedList(), IntersectionType.class, false);
        }
        typeVariables.add(type);
    }

    public boolean hasTypeVariable(String name){
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

    public void typeVariables(List vars){
        typeVariables = vars;
    }
    
    public boolean isGeneric(){
        if ((typeVariables != null) && !typeVariables.isEmpty()) return true;
        return false;
    }

    public boolean callValidImpl(List argTypes){
        if (this.isGeneric()){
            try {
                List inferredTypes = ((JL5TypeSystem)typeSystem()).inferTypesFromArgs(typeVariables(), formalTypes(), argTypes, new ArrayList());
                return genericCallValidImpl(argTypes, inferredTypes);
            }
            catch(SemanticException e){
                return false;
            }
        }
        List l1 = this.formalTypes();
        List l2 = argTypes;

        for (int i = 0; i < l1.size(); i++){
            Type t1 = (Type)l1.get(i);
            if (l2.size() > i){
                Type t2 = (Type)l2.get(i);
           

                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    if (ts.isImplicitCastValid(t2, t1)){
                        return true;
                    }
                    for (int j = i; j < l2.size(); j++){
                        Type tv = (Type)l2.get(j);
                        if (!ts.isImplicitCastValid(tv, ((ArrayType)t1).base())){
                            return false;
                        }
                    }
                }
                else {
                    if (!ts.isImplicitCastValid(t2, t1)) {
                        return false;
                    }
                }
            }
            else {
                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return true; 
    }

    public boolean canOverrideImpl(MethodInstance mj, boolean quiet) throws SemanticException{
        MethodInstance mi = this;

        if (!(mi.name().equals(mj.name()) && mi.hasFormals(mj.formalTypes()))) {
            if (quiet) return false;
            throw new SemanticException("Arguments are different", mi.position());
        }


        // changed to isSubtype may need to add bridge methods - even when no generics used - covariant return types 
        if (! ts.isSubtype(mi.returnType(), mj.returnType())){// && !((JL5TypeSystem)ts).isEquivalent(mi.returnType(), mj.returnType())) {
            if (quiet) return false;
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; attempting to use incompatible " +
                                        "return type\n" +                                        
                                        "found: " + mi.returnType() + "\n" +
                                        "required: " + mj.returnType(), 
                                        mi.position());
        } 

        if (! ts.throwsSubset(mi, mj)) {
            if (quiet) return false;
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; the throw set is not a subset of the " +
                                        "overridden method's throw set", 
                                        mi.position());
        }   

        if (mi.flags().moreRestrictiveThan(mj.flags())) {
            if (quiet) return false;
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; attempting to assign weaker " + 
                                        "access privileges", 
                                        mi.position());
        }

        if (mi.flags().isStatic() != mj.flags().isStatic()) {
            if (quiet) return false;
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; overridden method is " + 
                                        (mj.flags().isStatic() ? "" : "not") +
                                        "static", 
                                        mi.position());
        }

        if (mi != mj && !mi.equals(mj) && mj.flags().isFinal()) {
	    // mi can "override" a final method mj if mi and mj are the same method instance.
            throw new SemanticException(mi.signature() + " in " + mi.container() +
                                        " cannot override " + 
                                        mj.signature() + " in " + mj.container() + 
                                        "; overridden method is final", 
                                        mi.position());
        }

        return true;
    }

    public boolean hasFormalsImpl(List formalTypes){
        List l1 = this.formalTypes();
        List l2 = formalTypes;

        Iterator i1 = l1.iterator();
        Iterator i2 = l2.iterator();

        while (i1.hasNext() && i2.hasNext()) {
            Type t1 = (Type) i1.next();
            Type t2 = (Type) i2.next();

            if (! ts.equals(t1, t2) && ! ((JL5TypeSystem)ts).isEquivalent(t1, t2)) {
                return false;
            }
        }

        return ! (i1.hasNext() || i2.hasNext());

    }

    public boolean genericMethodCallValidImpl(String name, List argTypes, List inferredTypes){
        return name().equals(name) && ((JL5TypeSystem)typeSystem()).genericCallValid(this, argTypes, inferredTypes);
    }
    
    // inferred types correspond to typeVars not to formals list
    public boolean genericCallValidImpl(List argTypes, List inferredTypes){
        List l1 = this.formalTypes();
        List l2 = argTypes;

        for (int i = 0; i < l1.size(); i++){
            Type t1 = (Type)l1.get(i);
            
            // handle inferred types
            if (t1 instanceof IntersectionType && typeVariables().contains(t1)){
                t1 = (Type)inferredTypes.get(typeVariables().indexOf(t1));
            }
            else if (t1 instanceof ParameterizedType){
                t1 = ((ParameterizedType)t1).convertToInferred(typeVariables(), inferredTypes);
            }
            
            if (l2.size() > i){
                Type t2 = (Type)l2.get(i);
           

                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    if (ts.isImplicitCastValid(t2, t1)){
                        return true;
                    }
                    for (int j = i; j < l2.size(); j++){
                        Type tv = (Type)l2.get(j);
                        if (!ts.isImplicitCastValid(tv, ((ArrayType)t1).base())){
                            return false;
                        }
                    }
                }
                else {
                    if (!ts.isImplicitCastValid(t2, t1)) {
                        return false;
                    }
                }
            }
            else {
                if (t1 instanceof JL5ArrayType && ((JL5ArrayType)t1).isVariable()){
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return true; 
    }

}
