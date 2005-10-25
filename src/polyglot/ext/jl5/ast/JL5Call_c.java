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
       
        // can only call a method with type args if it was declared as generic
        if (!typeArguments.isEmpty() && !((JL5MethodInstance)n.methodInstance()).isGeneric()) {
            throw new SemanticException("Cannot call method: "+n.methodInstance().name()+" with type arguments", position());
        }
        
        JL5MethodInstance mi = (JL5MethodInstance)n.methodInstance();
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        
        // wildcards are not allowed for type args for generic call
        for (int i = 0; i < typeArguments.size(); i++) {
            TypeNode correspondingArg = (TypeNode)typeArguments.get(i);
            if (correspondingArg instanceof BoundedTypeNode){
                throw new SemanticException("Wilcard argument not allowed here", correspondingArg.position());
            }
        }

        // type check call arguments
        if (target() != null && target().type() instanceof ParameterizedType){
            //System.out.println("target is param");
            for (int i = 0; i < mi.formalTypes().size(); i++){
                Type t = (Type)mi.formalTypes().get(i);
                if (t instanceof IntersectionType){
                    Type other = ts.findRequiredType((IntersectionType)t, (ParameterizedType)target().type());
                    if (!ts.isImplicitCastValid(((Expr)arguments().get(i)).type(), other)){
                        throw new SemanticException("Found arg of type: "+((Expr)arguments().get(i)).type()+" expected: "+other, ((Expr)arguments().get(i)).position());
                    }
                }
            }
        

            //System.out.println("mi.return type is a: "+mi.returnType().getClass());
            // set return type
            if (mi.returnType() instanceof IntersectionType){
                Type other = ts.findRequiredType((IntersectionType)mi.returnType(), (ParameterizedType)target().type());
                return n.type(other);
            }

            // this has to be done recursively on IntersectionType args
            // of parameterized types
            /*else if (mi.returnType() instanceof ParameterizedType){
                for (Iterator it = ((ParameterizedType)mi.returnType()).typeArguments(); it.hasNext()){
                    Type next = (Type)it.next();
                    if (next instanceof IntersectionType
                }
            }*/
        }
        return n.type(mi.returnType());
        
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
