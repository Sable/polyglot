package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.types.*;
import java.util.*;
import polyglot.visit.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;

public class JL5ConstructorDecl_c extends ConstructorDecl_c implements JL5ConstructorDecl, ApplicationCheck{

    protected List annotations;
    protected List paramTypes;
    
    public JL5ConstructorDecl_c(Position pos, FlagAnnotations flags, String name, List formals, List throwTypes, Block body) {
        super(pos, flags.classicFlags(), name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
    }

    public JL5ConstructorDecl_c(Position pos, FlagAnnotations flags, String name, List formals, List throwTypes, Block body, List paramTypes){
        super(pos, flags.classicFlags(), name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        this.paramTypes = paramTypes;
    }

    public List paramTypes(){
        return this.paramTypes;
    }

    public JL5ConstructorDecl paramTypes(List paramTypes){
        JL5ConstructorDecl_c n = (JL5ConstructorDecl_c) copy();
        n.paramTypes = paramTypes;
        return n;
    }

    protected JL5ConstructorDecl_c reconstruct(List formals, List throwTypes, Block body, List annotations, List paramTypes){
        if (! CollectionUtil.equals(formals, this.formals) || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body || !CollectionUtil.equals(annotations, this.annotations) || !CollectionUtil.equals(paramTypes, this.paramTypes)) {
            JL5ConstructorDecl_c n = (JL5ConstructorDecl_c) copy();
            n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
            n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
            n.paramTypes = paramTypes;
            return n;
        }
        return this;

    }

    public Node visitChildren(NodeVisitor v){
        List formals = visitList(this.formals, v);
        List throwTypes = visitList(this.throwTypes, v);
        Block body = (Block) visitChild(this.body, v);
        List annotations = visitList(this.annotations, v);
        List paramTypes = visitList(this.paramTypes, v);
        return reconstruct(formals, throwTypes, body, annotations, paramTypes);
    }


    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == JL5AmbiguityRemover.TYPE_VARS) {
            return ar.bypass(formals).bypass(throwTypes).bypass(body);
        }
        else {
            return super.disambiguateEnter(ar);
        }
    }

    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        w.write(flags().translate());

        if ((paramTypes != null) && !paramTypes.isEmpty()){
            w.write("<");
            for (Iterator it = paramTypes.iterator(); it.hasNext(); ){
                ParamTypeNode next = (ParamTypeNode)it.next();
                print(next, w, tr);
                if (it.hasNext()){
                    w.write(", ");
                }
            }
            w.write("> ");
        }
        
        w.write(name);
        w.write("(");
        w.begin(0);

        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            Formal f = (Formal) i.next();
            print(f, w, tr);

            if (i.hasNext()) {
            w.write(",");
            w.allowBreak(0, " ");
            }
        }

        w.end();
        w.write(")");

        if (! throwTypes().isEmpty()) {
            w.allowBreak(6);
            w.write("throws ");

            for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);

                if (i.hasNext()) {
                    w.write(",");
                    w.allowBreak(4, " ");
                }
            }
        }
        w.end();
    }

    public List annotations(){
        return this.annotations;
    }

    public JL5ConstructorDecl annotations(List annotations){
        JL5ConstructorDecl_c n = (JL5ConstructorDecl_c) copy();
        n.annotations = annotations;
        return n;
    }
    
    public Node addMembers(AddMemberVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        return addCCallIfNeeded(ts, nf);
    }

    protected Node addCCallIfNeeded(TypeSystem ts, NodeFactory nf){
        if (cCallNeeded()){
            return addCCall(ts, nf);        
        }
        return this;
    }

    protected boolean cCallNeeded(){
        if (!body.statements().isEmpty() && body.statements().get(0) instanceof ConstructorCall || JL5Flags.isEnumModifier(((ClassType)constructorInstance().container()).flags())) return false;
        return true;
    }

    protected Node addCCall(TypeSystem ts, NodeFactory nf){
        ConstructorInstance sci = ts.defaultConstructor(position(), (ClassType) this.constructorInstance().container().superType());
        ConstructorCall cc = nf.SuperCall(position(), Collections.EMPTY_LIST);
        cc = cc.constructorInstance(sci); 
        body.statements().add(0, cc);
        return reconstruct(formals, throwTypes, body);
        
    }

    public Node applicationCheck(ApplicationChecker appCheck) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)appCheck.typeSystem();
        for( Iterator it = annotations.iterator(); it.hasNext(); ){
            AnnotationElem next = (AnnotationElem)it.next();
            ts.checkAnnotationApplicability(next, this);
        }
        return this;
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
    
        for (Iterator it = annotations.iterator(); it.hasNext(); ){
            print((AnnotationElem)it.next(), w, tr);
        }
        
        super.prettyPrint(w, tr);
    }
}
