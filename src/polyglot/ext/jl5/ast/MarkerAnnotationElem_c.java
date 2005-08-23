package polyglot.ext.jl5.ast;

import java.util.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.ast.*;
import polyglot.types.*;

public class MarkerAnnotationElem_c extends NormalAnnotationElem_c implements MarkerAnnotationElem {

    public MarkerAnnotationElem_c(Position pos, TypeNode typeName){
        super(pos, typeName, new TypedList(new LinkedList(), ElementValuePair.class, false));
    }

    public Node visitChildren(NodeVisitor v){
        return super.visitChildren(v);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return super.typeCheck(tc);
    }
    
    public void translate(CodeWriter w, Translator tr){
        super.translate(w, tr);
    }
}
