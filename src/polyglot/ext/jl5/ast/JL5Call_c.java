package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5Call_c extends Call_c implements JL5Call {

    protected List typeArguments;

    public JL5Call_c(Position pos, Receiver target, String name, List arguments, List typeArguments){
        super(pos, target, name, arguments);
        this.typeArguments = typeArguments;
    }
    
    public List typeArguments(){
        return typeArguments;
    }
    
    public JL5Call typeArguments(List args){
        JL5Call_c n = (JL5Call_c) copy();
        n.typeArguments = args;
        return n;
    }

    public JL5Call_c reconstruct(Receiver target, List arguments, List typeArgs) {
        if (target != this.target || ! CollectionUtil.equals(arguments, this.arguments) || ! CollectionUtil.equals(typeArgs, this.typeArguments)){
            JL5Call_c n = (JL5Call_c)copy();
            n.target = target;
            n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
            n.typeArguments = TypedList.copyAndCheck(typeArgs, TypeNode.class, false);
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        Receiver target = (Receiver) visitChild(this.target, v);
        List arguments = visitList(this.arguments, v);
        List typeArgs = visitList(this.typeArguments, v);
        return reconstruct(target, arguments, typeArgs);
    }
    
 
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5Call_c n = (JL5Call_c)super.typeCheck(tc);

        return checkTypeArguments(tc, n);
    }

    private Node checkTypeArguments(TypeChecker tc, JL5Call_c n) throws SemanticException {
        // check that each type arg is a subtype of the mi typeVars list
        // recheck args and 
        // reset this type
       
        /*for (Iterator it = mi.formalTypes().iterator(); it.hasNext(); ){
            Type next = (Type)it.next();
            if (next instanceof IntersectionType){
            }
        }*/
        
        if (!typeArguments.isEmpty() && !((JL5MethodInstance)n.methodInstance()).isGeneric()) {
            throw new SemanticException("Cannot call method: "+n.methodInstance().name()+" with type arguments", position());
        }
        JL5MethodInstance gmi = (JL5MethodInstance)n.methodInstance();
        
        /*if (typeArguments.size() != 0 && typeArguments.size() != gmi.typeVariables().size()){
            throw new SemanticException("Must specify all type arguments of none", position());
        }*/

        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        //Call gCall = this.methodInstance(gmi);
        
        // here return gCall with restricted return type if necessary
        //if (typeArguments.size() == 0) return gCall;
        //
        // add restrictions 
        for (int i = 0; i < typeArguments.size(); i++) {
            TypeNode correspondingArg = (TypeNode)typeArguments.get(i);
            if (correspondingArg instanceof BoundedTypeNode){
                throw new SemanticException("Wilcard argument not allowed here", correspondingArg.position());
            }
        }
        /*for (int j = 0; j < arguments().size(); j++){
            Expr next = (Expr)arguments().get(j);
            Type formalType = (Type)gmi.formalTypes().get(j);
            if (formalType instanceof IntersectionType && !((IntersectionType)formalType).restrictions().isEmpty()){
                List list = ((IntersectionType)formalType).restrictions();
                TypeNode restriction = (TypeNode)list.get(list.size()-1);
                // there is a new restrictive type
                if (!ts.isSubtype(next.type(), restriction.type())){
                    throw new SemanticException("Arg has incorrect type: "+restriction+" expected", next.position());
                }
            }
        }*/
        if (gmi.returnType() instanceof IntersectionType && ((IntersectionType)gmi.returnType()).restriction() != null){
            TypeNode restriction = ((IntersectionType)gmi.returnType()).restriction();
            return n.type(restriction.type());
        }
        else if (gmi.returnType() instanceof IntersectionType){
            return n.type(((IntersectionType)gmi.returnType()).superType());
        }
        else {
            return n.type(gmi.returnType());
        }
        
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        if (!targetImplicit){
            if (target instanceof Expr) {
                printSubExpr((Expr) target, w, tr);
                w.write(".");
            }
            else if (target != null) {
                print(target, w, tr);
                w.write(".");
            }
        }
        
        if (typeArguments.size()!=0){
            w.write("<");
            for (Iterator it = typeArguments.iterator(); it.hasNext(); ){
                print((TypeNode)it.next(), w, tr);
                if (it.hasNext()){
                    w.write(", ");
                }
            }
            w.write(">");
        }
       
        w.write(name + "(");
        w.begin(0);

        for(Iterator i = arguments.iterator(); i.hasNext();) {
            Expr e = (Expr) i.next();
            print(e, w, tr);
            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }
        w.end();
        w.write(")");
                        
    }
}
