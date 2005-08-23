package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import java.util.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.types.*;

public class JL5Formal_c extends Formal_c implements JL5Formal {

    protected List annotations;
    
    public JL5Formal_c(Position pos, FlagAnnotations flags, TypeNode type, String name){
        super(pos, flags.classicFlags(), type, name);
        this.annotations = flags.annotations();
    }
    
    public List annotations(){
        return annotations;
    }
    
    public JL5Formal annotations(List annotations){
        JL5Formal_c n = (JL5Formal_c) copy();
        n.annotations = annotations;
        return n;
    }

    protected Formal reconstruct(TypeNode type, List annotations){
        if (this.type() != type || !CollectionUtil.equals(annotations, this.annotations)){
            JL5Formal_c n = (JL5Formal_c)copy();
            n.type(type);
            n.annotations = annotations;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode type = (TypeNode)visitChild(this.type(), v);
        List annots = visitList(this.annotations, v);
        return reconstruct(type, annots);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (!flags().clear(Flags.FINAL).equals(Flags.NONE)){
            throw new SemanticException("Modifier: "+flags().clearFinal()+" not allowed here.", position());
        }
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
