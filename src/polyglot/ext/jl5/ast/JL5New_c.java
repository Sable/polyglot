package polyglot.ext.jl5.ast;

import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import java.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.ast.*;
import polyglot.visit.*;

public class JL5New_c extends New_c implements JL5New {

    protected List typeArguments;
    
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
            n.typeArguments = TypedList.copyAndCheck(typeArgs, TypeNode.class, false);
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

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (tn.type().isClass()){
            ClassType ct = (ClassType)tn.type();
            if (JL5Flags.isEnumModifier(ct.flags())){
                throw new SemanticException("Cannot instantiate an enum type.",  this.position());
            }
        }
        return super.typeCheck(tc);
    }

    protected Node typeCheckEpilogue(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        JL5New_c n = (JL5New_c)super.typeCheckEpilogue(tc);
        if (n.type() instanceof ParameterizedType){
            // should check arguments here - if any are intersection types
            // in the methodInstance then they need to be checked against 
            // the typeArgs
            ConstructorInstance ci = constructorInstance();
            for (int i = 0; i < ci.formalTypes().size(); i++ ){
                Type t = (Type)ci.formalTypes().get(i);
                if (t instanceof IntersectionType){
                    Type other = ts.findRequiredType((IntersectionType)t, (ParameterizedType)n.type());
                    if (!ts.isImplicitCastValid(((Expr)arguments().get(i)).type(), other)){
                        throw new SemanticException("Found arg of type: "+((Expr)arguments().get(i)).type()+" expected: "+other, ((Expr)arguments().get(i)).position());
                    }
                }
            }
        }

        return n;
    }
}
