package polyglot.ext.jl5.ast;

import polyglot.ast.*;
import polyglot.ext.jl.ast.*;
import polyglot.ext.jl5.types.*;
import polyglot.util.*;
import polyglot.types.*;
import polyglot.visit.*;
import java.util.*;

public class AnnotationElem_c extends Expr_c implements AnnotationElem {

    protected TypeNode typeName;
    
    public AnnotationElem_c(Position pos, TypeNode typeName){
        super(pos);
        this.typeName = typeName;
    }

    public TypeNode typeName(){
        return typeName;
    }

    public AnnotationElem typeName(TypeNode typeName){
        if (!typeName.equals(this.typeName)){
            AnnotationElem_c n = (AnnotationElem_c) copy();
            n.typeName = typeName;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v){
        TypeNode tn = (TypeNode)visitChild(this.typeName, v);
        return typeName(tn);
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (!typeName.type().isClass() || !JL5Flags.isAnnotationModifier(((JL5ParsedClassType)typeName.type()).flags())){
            throw new SemanticException("Annotation: "+typeName+" must be an annotation type, ", position());
                    
        }
        return type(typeName.type());
    }
    
    public void translate(CodeWriter w, Translator tr){
        w.write("@");
        print(typeName, w, tr);
    }
    
    public Term entry() {
        return this;
    }
    
    public List acceptCFG(CFGBuilder v, List succs) {
        return succs;
    }
        
}
