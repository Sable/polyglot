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
        return f;
        
    }

    public void checkConsistency(Context c){
        super.checkConsistency(c);
    }
}
