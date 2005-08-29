package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;

public class JL5LocalDecl_c extends LocalDecl_c implements JL5LocalDecl{

    protected List annotations;
    
    public JL5LocalDecl_c(Position pos, FlagAnnotations flags, TypeNode type, String name, Expr init){
        super(pos, flags.classicFlags(), type, name, init);
        if (flags.annotations() != null){
            this.annotations = flags.annotations();
        }
        else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, true);
        }
    }
    
    public List annotations(){
        return annotations;
    }
    
    public JL5LocalDecl annotations(List annotations){
        JL5LocalDecl_c n = (JL5LocalDecl_c) copy();
        n.annotations = annotations;
        return n;
    }

    protected LocalDecl reconstruct(TypeNode type, Expr init, List annotations){
        if (this.type() != type || this.init() != init ||  !CollectionUtil.equals(annotations, this.annotations)){
            JL5LocalDecl_c n = (JL5LocalDecl_c)copy();
            n.type(type);
            n.init(init);
            n.annotations = annotations;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode type = (TypeNode)visitChild(this.type(), v);
        Expr init = (Expr)visitChild(this.init(), v);
        List annots = visitList(this.annotations, v);
        return reconstruct(type, init, annots);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (!flags().clear(Flags.FINAL).equals(Flags.NONE)){
            throw new SemanticException("Modifier: "+flags().clearFinal()+" not allowed here.", position());
        }
        JL5TypeSystem ts = (JL5TypeSystem)tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);
        return super.typeCheck(tc);
    }
    
    public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        if (annotations != null){
            for (Iterator it = annotations.iterator(); it.hasNext(); ){
                print((AnnotationElem)it.next(), w, tr);
            }
        }
        super.prettyPrint(w, tr);
    }
}
