package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;
import polyglot.visit.*;

public class GenericNew_c extends JL5New_c implements GenericNew {

    protected List typeArguments;

    public GenericNew_c(Position pos, Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArguments){
        super(pos, qualifier, tn, arguments, body);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public GenericNew typeArguments(List args){
        GenericNew_c n = (GenericNew_c) copy();
        n.typeArguments = args;
        return n;
    }
   
    /** Reconstruct the expression. */
    protected GenericNew_c reconstruct(Expr qualifier, TypeNode tn, List arguments, ClassBody body, List typeArgs) {
        if (qualifier != this.qualifier || tn != this.tn || ! CollectionUtil.equals(arguments, this.arguments) || body != this.body || ! CollectionUtil.equals(typeArgs, this.typeArguments)) {
            GenericNew_c n = (GenericNew_c) copy();
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
        
    /*public Node buildTypes(TypeBuilder tb) throws SemanticException {
        
    }*/
}
