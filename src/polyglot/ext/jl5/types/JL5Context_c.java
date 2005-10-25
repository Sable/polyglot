package polyglot.ext.jl5.types;

import polyglot.ext.jl.types.*;
import polyglot.types.*;
import java.util.*;
import polyglot.util.*;

public class JL5Context_c extends Context_c implements JL5Context {

    protected Map typeVars;
    protected IntersectionType typeVariable;

    public static final Kind TYPE_VAR = new Kind("type-var");
    
    public JL5Context_c(TypeSystem ts){
        super(ts);
    }
    
    public VarInstance findVariableInThisScope(String name){
        
        VarInstance vi = null;
        //try{
        vi = super.findVariableInThisScope(name);
        //}
        //catch(NoMemberException e){
            
        if (vi == null && isClass()){
            try {
                return ((JL5TypeSystem)typeSystem()).findEnumConstant(this.type, name, this.type);
            }
            catch (SemanticException e2){
                return null;
            }
        }
        return vi;
    }

    public VarInstance findVariableSilent(String name){
        VarInstance vi = findVariableInThisScope(name);
        if (vi != null){
            return vi;
        }

        try {
        // might be static
        if (importTable() != null){
            JL5ImportTable jit = (JL5ImportTable)importTable();
            for (Iterator it = jit.memberImports().iterator(); it.hasNext(); ){
                String next = (String)it.next();
                String id = StringUtil.getShortNameComponent(next);
                if (name.equals(id)){
                    Named nt = ts.forName(StringUtil.getPackageComponent(next));
                    if (nt instanceof Type){
                        Type t = (Type)nt;
                        try {
                            vi = ts.findField(t.toClass(), name);
                        }
                        catch(SemanticException e){}
                        if (vi != null){
                            return vi;
                        }
                    }
                }
            }
            if (vi == null){
                for (Iterator it = jit.staticClassImports().iterator(); it.hasNext(); ){
                    String next = (String)it.next();
                    Named nt = ts.forName(next);
                    if (nt instanceof Type){
                        Type t = (Type)nt;
                        try {
                            vi = ts.findField(t.toClass(), name);
                        }
                        catch(SemanticException e){
                        }
                        if (vi != null) return vi;
                    }
                }
            }
        }
        }
        catch(SemanticException e){
        }
        
        if (outer != null){
            return outer.findVariableSilent(name);
        }
        return null;
    }

    public Named findInThisScope(String name){
        if (types != null){
            for (Iterator typesIt = types.keySet().iterator(); typesIt.hasNext();){
                String nextType = (String)typesIt.next();
                Named next = (Named)types.get(nextType);
                if (next instanceof JL5ParsedClassType && ((JL5ParsedClassType)next).isGeneric()){
                    JL5ParsedClassType ct = (JL5ParsedClassType)next;
                    if (ct.hasTypeVariable(name)){
                        return ct.getTypeVariable(name);
                    }
                }
            }
        }
        return super.findInThisScope(name);
    }

    public MethodInstance findMethod(String name, List argTypes) throws SemanticException {
        if (this.currentClass() != null && ts.hasMethodNamed(this.currentClass(), name)){
            return ts.findMethod(this.currentClass(), name, argTypes, this.currentClass());
        }

        if (importTable() != null){
        JL5ImportTable jit = (JL5ImportTable)importTable();
        for (Iterator it = jit.memberImports().iterator(); it.hasNext(); ){
            String next = (String)it.next();
            String id = StringUtil.getShortNameComponent(next);
            if (name.equals(id)){
                Named nt = ts.forName(StringUtil.getPackageComponent(next));
                if (nt instanceof Type){
                    Type t = (Type)nt;
                    if (t.isClass() && ts.hasMethodNamed(t.toClass(), name)){
                        return ts.findMethod(t.toClass(), name, argTypes, this.currentClass());
                    }
                }
            }
        }

        for (Iterator it = jit.staticClassImports().iterator(); it.hasNext(); ){
            String next = (String)it.next();
            Named nt = ts.forName(next);
            if (nt instanceof Type){
                Type t = (Type)nt;
                if (t.isClass() && ts.hasMethodNamed(t.toClass(), name)){
                    return ts.findMethod(t.toClass(), name, argTypes, this.currentClass());
                }
            }
        }
        }
        if (outer != null){
            return outer.findMethod(name, argTypes);
        }

        throw new SemanticException("Method "+name+" not found.");
    }
    
    public JL5Context pushTypeVariable(IntersectionType iType){
        //System.out.println("pushing type var context: "+iType.name());
        JL5Context_c v = (JL5Context_c)push();
        v.typeVariable = iType;
        v.kind = TYPE_VAR;
        //System.out.println("v: "+v);
        //v.outer = this;
        return v;
    }
    
    public IntersectionType findTypeVariableInThisScope(String name){
        //System.out.println("search for: "+name+" in: "+this);
        if (typeVariable != null && typeVariable.name().equals(name)) return typeVariable;
        //System.out.println("outer: "+((JL5Context)outer).inTypeVariable());
        if (typeVars != null && typeVars.containsKey(name)) return (IntersectionType)typeVars.get(name);
        if (outer != null){
            return ((JL5Context)outer).findTypeVariableInThisScope(name);
        }
        return null;
    }

    public boolean inTypeVariable(){
        //System.out.println("kind in inTypeVariable :"+kind);
        return kind == TYPE_VAR;
    }

    public String toString(){
        return super.toString() + "type var: "+typeVariable;
    }

    public JL5Context addTypeVariable(IntersectionType type){
        if (typeVars == null) typeVars = new HashMap();
        typeVars.put(type.name(), type);
        return this;
    }
}
