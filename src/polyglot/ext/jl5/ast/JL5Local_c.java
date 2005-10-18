package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;

public class JL5Local_c extends Local_c implements JL5Local{

    public JL5Local_c(Position pos, String name){
        super(pos, name);
    }

    /*public Node typeCheck(TypeChecker tc) throws SemanticException {
        System.out.println("checking local: "+this);
        Context c = tc.context();
        JL5ParsedClassType ct = (JL5ParsedClassType)c.currentClassScope();
        System.out.println("ct class: "+ct.getClass());
        System.out.println("anon: "+ct.isAnonymous());
        System.out.println("class: "+ct+" is a: "+ct.getClass());
        if (ct.outer() != null){
            System.out.println("class outer: "+ct.outer()+" is a: "+ ct.outer().getClass());
            //System.out.println("class args :" +ct.typeArguments());
        }
        ReferenceType st = (ReferenceType)ct.superType();
        while (st != null){
            System.out.println("class super: "+st+" is a: "+ st.getClass());
            st = (ReferenceType)st.superType();
        }
        System.out.println(this.type());
        Local n = (Local)super.typeCheck(tc);
        System.out.println(n.type()+" is a: "+n.type().getClass());
        return n;
        
        
    }*/
}
