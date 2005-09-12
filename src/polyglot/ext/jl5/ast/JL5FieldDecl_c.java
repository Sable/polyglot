package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;
import polyglot.ext.jl5.types.*;
import polyglot.ext.jl5.visit.*;
import polyglot.types.*;
import java.util.*;
import polyglot.visit.*;

public class JL5FieldDecl_c extends FieldDecl_c implements JL5FieldDecl, ApplicationCheck {

    protected List annotations;
    
    public JL5FieldDecl_c(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init){
        super(pos, flags.classicFlags(), type, name, init);
        if (flags.annotations() != null){
            annotations = flags.annotations();
        }
        else {
            annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
    }

    public List annotations(){
        return this.annotations;
    }

    public JL5FieldDecl annotations(List annotations){
        JL5FieldDecl_c n = (JL5FieldDecl_c) copy();
        n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
        return n;
    }
    
    protected JL5FieldDecl reconstruct(TypeNode type, Expr init, List annotations){
        if( this.type() != type || this.init() != init || !CollectionUtil.equals(this.annotations, annotations)){
            JL5FieldDecl_c n = (JL5FieldDecl_c) copy();
            n.type = type;
            n.init = init;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, true);
            return n;
        }
        return this;
    }
    
    public Node visitChildren(NodeVisitor v){
        TypeNode type = (TypeNode) visitChild(this.type(), v);
        Expr init = (Expr) visitChild(this.init(), v);
        List annotations = visitList(this.annotations, v);
        return reconstruct(type, init, annotations);
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        return super.typeCheck(tc);
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
