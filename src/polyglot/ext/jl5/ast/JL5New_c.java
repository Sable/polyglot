package polyglot.ext.jl5.ast;

import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import java.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.frontend.*;

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
    
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() != AmbiguityRemover.ALL) {
            return this;
        }

        if (qualifier == null) {
            ClassType ct = tn.type().toClass();

            if (! ct.isMember() || ct.flags().isStatic()) {
                return this;
            }

            // If we're instantiating a non-static member class, add a "this"
            // qualifier.
            NodeFactory nf = ar.nodeFactory();
            TypeSystem ts = ar.typeSystem();
            Context c = ar.context();

            // Search for the outer class of the member.  The outer class is
            // not just ct.outer(); it may be a subclass of ct.outer().
            Type outer = null;

            String name = ct.name();
            ClassType t = c.currentClass();

            // We're in one scope too many.
            if (t == anonType) {
                t = t.outer();
            }

            while (t != null) {
                try {
                    // HACK: PolyJ outer() doesn't work
                    t = ts.staticTarget(t).toClass();
                    ClassType mt = ts.findMemberClass(t, name, c.currentClass());
                    
                    if (ts.equals(mt, ct) || (ct instanceof ParameterizedType && ts.equals(mt, ((ParameterizedType)ct).baseType()))) {
                        outer = t;
                        break;
                    }
                }
                catch (SemanticException e) {
                }

                t = t.outer();
            }

            if (outer == null) {
                throw new SemanticException("Could not find non-static member class \"" +
                                            name + "\".", position());
            }

            // Create the qualifier.
            Expr q;

            if (outer.equals(c.currentClass())) {
                q = nf.This(position());
            }
            else {
                q = nf.This(position(),
                            nf.CanonicalTypeNode(position(),
                                                 outer));
            }

            return qualifier(q);
        }

        return this;
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
            // check no wilcards
            for (Iterator it = ((ParameterizedType)n.type()).typeArguments().iterator(); it.hasNext(); ){
                Type tn = (Type)it.next();
                if (tn instanceof AnyType || tn instanceof AnySubType || tn instanceof AnySuperType){
                    throw new SemanticException("Unexpected type", n.position());
                }
            }
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

    protected TypeNode partialDisambTypeNode(TypeNode tn, TypeChecker tc, ClassType outer) throws SemanticException {
        if (tn instanceof CanonicalTypeNode) {
            return tn;
        }

        String name = null;

        if (tn instanceof AmbTypeNode && ((AmbTypeNode) tn).qual() == null) {
            name = ((AmbTypeNode) tn).name();
        }
        else {
            throw new SemanticException(
                "Cannot instantiate an member class.",
                tn.position());
        }
        
        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        Context c = tc.context();


        ClassType ct = ts.findMemberClass(outer, name, c.currentClass());
        if (tn instanceof JL5AmbTypeNode){
            
            ParameterizedType pt = new ParameterizedType_c((JL5ParsedClassType)ct);
            ArrayList typeArgs = new ArrayList(((JL5AmbTypeNode)tn).typeArguments().size());
            for (Iterator it = ((JL5AmbTypeNode)tn).typeArguments().iterator(); it.hasNext(); ){
                TypeNode arg = (TypeNode)it.next();
                    Job sj = tc.job().spawn(tc.context(), arg, Pass.CLEAN_SUPER, Pass.DISAM_ALL);
                    if (! sj.status()) {
                        if (! sj.reportedErrors()) {
                            throw new SemanticException("Could not disambiguate type.", this.tn.position());
                        }
                        throw new SemanticException();
                    }
                arg = (TypeNode)sj.ast();
                typeArgs.add(arg.type());
            }
            pt.typeArguments(typeArgs);
            
            CanonicalTypeNode ctn = nf.CanonicalTypeNode(tn.position(), pt);
            return ctn; 
        }
        return nf.CanonicalTypeNode(tn.position(), ct);
    }
             
}
