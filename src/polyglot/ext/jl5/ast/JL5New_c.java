package polyglot.ext.jl5.ast;

import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import java.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.ast.*;
import polyglot.visit.*;

public class JL5New_c extends New_c implements JL5New {

    protected List typeArguments;
    
    public JL5New_c (Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body){
        super(pos, qualifier, tn, arguments, body);
    }

    public JL5New_c(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArguments){
        super(pos, qualifier, tn, arguments, body);
        this.typeArguments = typeArguments;
    }

    public List typeArguments(){
        return typeArguments;
    }

    public JL5New typeArguments(List args){
        JL5New_c n = (JL5New_c) copy();
        n.typeArguments = args;
        return n;
    }

    /** Reconstruct the expression. */
    protected JL5New_c reconstruct(Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArgs) {
        if (qualifier != this.qualifier || tn != this.tn || ! CollectionUtil.equals(arguments, this.arguments) || body != this.body || ! CollectionUtil.equals(typeArgs, this.typeArguments)) {
            JL5New_c n = (JL5New_c) copy();
            n.tn = tn;
            n.qualifier = qualifier;
            n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
            n.body = body;
            n.typeArguments = typeArgs;
            return n;
        }
        return this;
    }

    /** Visit the children of the expression. */
    public Node visitChildren(NodeVisitor v) {
        Expr qualifier = (Expr) visitChild(this.qualifier, v);
        TypeNode tn = (TypeNode) visitChild(this.tn, v);
        List arguments = visitList(this.arguments, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        List typeArgs = visitList(this.typeArguments, v);
        return reconstruct(qualifier, tn, arguments, body, typeArgs);
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        New_c n = (New_c) super.buildTypes(tb);
        JL5TypeSystem ts = (JL5TypeSystem)tb.typeSystem();

        List l = new ArrayList(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            l.add(ts.unknownType(position()));
        }

        List typeVars = new ArrayList(typeArguments.size());
        for (int i = 0; i < typeArguments.size(); i++){
            typeVars.add(ts.unknownType(position()));
        }
        
        ConstructorInstance ci = ts.constructorInstance(position(), ts.Object(), Flags.NONE, l, Collections.EMPTY_LIST, typeVars);
        return n.constructorInstance(ci);

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
