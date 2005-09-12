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
    
    public JL5ConstructorDecl_c(Position pos, FlagAnnotations flags, String name, List formals, List throwTypes, Block body) {
        super(pos, flags.classicFlags(), name, formals, throwTypes, body);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
    }

    public List annotations(){
        return this.annotations;
    }

    public JL5ConstructorDecl annotations(List annotations){
        JL5ConstructorDecl_c n = (JL5ConstructorDecl_c) copy();
        n.annotations = annotations;
        return n;
    }
    
    protected ConstructorDecl_c reconstruct(List formals, List throwTypes, Block body, List annotations) {
        if (!CollectionUtil.equals(formals, this.formals) || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body || ! CollectionUtil.equals(annotations, this.annotations)){
            JL5ConstructorDecl_c n = (JL5ConstructorDecl_c) copy();
            n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
            n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        List formals = visitList(this.formals, v);
        List throwTypes = visitList(this.throwTypes, v);
        Block body = (Block)visitChild(this.body, v);
        List annotations = visitList(this.annotations, v);
        return reconstruct(formals, throwTypes, body, annotations);
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
    
}
