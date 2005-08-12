package polyglot.ext.jl5.ast;

import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import java.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.ast.*;
import polyglot.visit.*;

public class JL5New_c extends New_c implements JL5New {

    public JL5New_c (Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body){
        super(pos, qualifier, tn, arguments, body);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (tn.type().isClass()){
            ClassType ct = (ClassType)tn.type();
            if (JL5Flags.isEnumModifier(ct.flags())){
                throw new SemanticException("Cannot instantiate an enum type.",  this.position());
            }
        }
        return super.typeCheck(tc);
    }
}
