package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import java.util.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;

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
    
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
         Call_c call = (Call_c) super.buildTypes(tb);

         JL5TypeSystem ts = (JL5TypeSystem)tb.typeSystem();

         List l = new ArrayList(arguments.size());
         for (int i = 0; i < arguments.size(); i++) {
            l.add(ts.unknownType(position()));
         }

         List typeVars = new ArrayList(typeArguments.size());
         for (int i = 0; i < typeArguments.size(); i++){
            typeVars.add(ts.unknownType(position()));
         }
                 

         MethodInstance mi = ts.methodInstance(position(), ts.Object(), Flags.NONE, ts.unknownType(position()), name, l, Collections.EMPTY_LIST, typeVars);
         return call.methodInstance(mi);
                             
    }

 
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5Call_c n = (JL5Call_c)super.typeCheck(tc);

        return checkTypeArguments(tc, n.methodInstance());
    }

    private Node checkTypeArguments(TypeChecker tc, MethodInstance mi) throws SemanticException {
        // check that each type arg is a subtype of the mi typeVars list
        // recheck args and 
        // reset this type
        
        if (!((JL5MethodInstance)mi).isGeneric()) {
            throw new SemanticException("Cannot call method: "+mi.name()+" with type arguments", position());
        }
        JL5MethodInstance gmi = (JL5MethodInstance)mi;
        
        if (typeArguments.size() != 0 && typeArguments.size() != gmi.typeVariables().size()){
            throw new SemanticException("Must specify all type arguments of none", position());
        }

        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        JL5Call_c gCall = this;
        if (typeArguments.size() == 0) return gCall;
        for (int i = 0; i < gmi.typeVariables().size(); i++) {
            IntersectionType foundType = (IntersectionType)gmi.typeVariables().get(i);            
            TypeNode correspondingArg = (TypeNode)typeArguments.get(i);
            if (correspondingArg instanceof BoundedTypeNode){
                throw new SemanticException("Wilcard argument not allowed here", correspondingArg.position());
            }
            if (!ts.isSubtype(correspondingArg.type(), foundType)){
                throw new SemanticException("Invalid type argument ",correspondingArg.position());
            }
            for (int j = 0; j < arguments().size(); j++){
                Expr next = (Expr)arguments().get(j);
                Type formalType = (Type)gmi.formalTypes().get(j);
                if (formalType instanceof IntersectionType && ((IntersectionType)formalType).name().equals(foundType.name())){
                    // there is a new restrictive type
                    if (!ts.isSubtype(next.type(), correspondingArg.type())){
                        throw new SemanticException("arg has incorrect type: ", next.position());
                    }
                }
            }
            if (gmi.returnType() instanceof IntersectionType && ((IntersectionType)gmi.returnType()).name().equals(foundType.name())){
                gCall = (JL5Call_c)this.type(correspondingArg.type());
            }
        }
       
        return gCall;
        
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
