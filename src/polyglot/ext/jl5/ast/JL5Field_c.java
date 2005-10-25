package polyglot.ext.jl5.ast;

import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.types.*;
import polyglot.visit.*;

public class JL5Field_c extends Field_c implements JL5Field {

    public JL5Field_c (Position pos, Receiver target, String name){
        super(pos, target, name);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Context c = tc.context();
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();

        if (! target.type().isReference()){
            throw new SemanticException("Cannot access field \"" + name +
             "\" " + (target instanceof Expr
             ? "on an expression "
             : "") +
             "of non-reference type \"" +
             target.type() + "\".", target.position());
        }

        FieldInstance fi = ts.findFieldOrEnum(target.type().toReference(), name, c.currentClass());

        if (fi == null) {
            throw new InternalCompilerError("Cannot access field on node of type "+ target.getClass().getName() + ".");
        }

        JL5Field_c f = (JL5Field_c)fieldInstance(fi).type(fi.type());
        f.checkConsistency(c);
        
        if (target() != null && target().type() instanceof ParameterizedType &&  fi.type() instanceof IntersectionType){
            Type other = ts.findRequiredType((IntersectionType)fi.type(), (ParameterizedType)target().type());
            return f.type(other);
        }

        if (target() != null && target().type() instanceof ClassType && ((ClassType)target().type()).isAnonymous() && fi.type() instanceof IntersectionType){
            Type other = ts.findRequiredType((IntersectionType)fi.type(), (ParameterizedType)((ClassType)target().type()).superType());
            return f.type(other);    
        }
       

        /*System.out.println("checking field: "+this);
        System.out.println("field target: "+this.target());
        System.out.println("field target kind: "+this.target().getClass());
        System.out.println("field target type: "+this.target().type());
        System.out.println("field target type class: "+this.target().type().getClass());*/
        /*JL5ParsedClassType ct = (JL5ParsedClassType)c.currentClass();
        System.out.println("ct class: "+ct.getClass());
        System.out.println("anon: "+ct.isAnonymous());
        System.out.println("class: "+ct+" is a: "+ct.getClass());
        ClassType out = ct.outer();
        while (out != null){
            System.out.println("class outer: "+out+" is a: "+ out.getClass());
            out = out.outer();
            //System.out.println("class args :" +ct.typeArguments());
        }*/
        /*ClassType st = (ClassType)((ClassType)this.target().type()).superType();
        while (st != null){
            //System.out.println("class super: "+st+" is a: "+ st.getClass());
            st = (ClassType)st.superType();
        }*/
        /*System.out.println(this.type());
        */
        
        return f;
        
    }

    public boolean isConstant(){
        if (JL5Flags.isEnumModifier(flags())) return true;
        if (fieldInstance() instanceof EnumInstance) return true;
        return super.isConstant();
    }
    public void checkConsistency(Context c){
        
        //super.checkConsistency(c);
        //this consistency checking has problems when dealing with gen
        //types
    }
}
